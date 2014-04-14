package de.metalcon.bootstrap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.hh.request_dispatcher.Callback;
import net.hh.request_dispatcher.Dispatcher;
import net.hh.request_dispatcher.RequestException;
import de.metalcon.api.responses.Response;
import de.metalcon.api.responses.SuccessResponse;
import de.metalcon.api.responses.errors.ErrorResponse;
import de.metalcon.bootstrap.domain.Entity;
import de.metalcon.bootstrap.domain.impl.Band;
import de.metalcon.bootstrap.domain.impl.Record;
import de.metalcon.bootstrap.domain.impl.Track;
import de.metalcon.bootstrap.parsers.BandAlbumCsvParser;
import de.metalcon.bootstrap.parsers.BandCsvParser;
import de.metalcon.bootstrap.parsers.RecordCsvParser;
import de.metalcon.exceptions.ServiceOverloadedException;
import de.metalcon.sdd.api.requests.SddReadRequest;
import de.metalcon.sdd.api.requests.SddWriteRequest;
import de.metalcon.sdd.api.responses.SddResponse;
import de.metalcon.sdd.api.responses.SddSucessfullQueueResponse;
import de.metalcon.urlmappingserver.api.requests.ResolveUrlRequest;
import de.metalcon.urlmappingserver.api.requests.UrlRegistrationRequest;

public class Bootstrap {

    public static final String SDD_SERVICE = "staticDataDeliveryServer";

    public static final String SDD_ENDPOINT = "tcp://127.0.0.1:1337";

    public static final String URL_MAPPING_SERVICE = "urlMappingServer";

    public static final String URL_MAPPING_SERVER_ENDPOINT =
            "tcp://127.0.0.1:12666";

    private Dispatcher dispatcher;

    private Map<Long, Band> bands = new HashMap<Long, Band>();

    private Map<Long, Record> records = new HashMap<Long, Record>();

    private Map<Long, Track> tracks = new HashMap<Long, Track>();

    //    private Map<Long, Image> images = new HashMap<Long, Image>();

    public static void main(String[] args) throws ServiceOverloadedException,
            InterruptedException, IOException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.load();
        //        bootstrap.run();
    }

    private void run() throws ServiceOverloadedException, InterruptedException,
            IOException {
        dispatcher = new Dispatcher();
        registerAdapters(dispatcher);

        SddWriteRequest sddWriteRequest = new SddWriteRequest();

        for (Band band : bands.values()) {
            band.fillSddWriteRequest(sddWriteRequest);
            registerUrl(band);
        }
        for (Record record : records.values()) {
            record.fillSddWriteRequest(sddWriteRequest);
            registerUrl(record);
        }
        for (Track track : tracks.values()) {
            track.fillSddWriteRequest(sddWriteRequest);
            registerUrl(track);
        }

        dispatcher.execute(sddWriteRequest, new Callback<SddResponse>() {

            @Override
            public void onError(RequestException exception) {
                exception.printStackTrace();
            }

            @Override
            public void onSuccess(SddResponse response) {
                if (response instanceof SddSucessfullQueueResponse) {
                    System.out.println("Queing data worked.");
                } else {
                    System.out.println("Queing data failed: "
                            + response.getClass());
                }
            }

        });

        dispatcher.gatherResults();
        dispatcher.shutdown();
    }

    private void registerUrl(final Entity entity) {
        UrlRegistrationRequest urlRequest =
                new UrlRegistrationRequest(entity.getUrlData());
        dispatcher.execute(urlRequest, new Callback<Response>() {

            @Override
            public void onSuccess(Response response) {
                if (response instanceof SuccessResponse) {
                    System.out.println("Url registration worked. ("
                            + entity.getClass().getSimpleName() + ":"
                            + entity.getName() + ")");
                } else {
                    System.out.println("Url registration failed. ("
                            + entity.getClass().getSimpleName() + ":"
                            + entity.getName() + ")");
                    System.out.println(response.getStatusMessage());
                    if (response instanceof ErrorResponse) {
                        System.out.println(((ErrorResponse) response)
                                .getErrorMessage());
                        System.out.println(((ErrorResponse) response)
                                .getSolution());
                    }
                }
            }

            @Override
            public void onError(RequestException e) {
                e.printStackTrace();
            }

        });
    }

    private void registerAdapters(Dispatcher dispatcher) {
        // StaticDataDelivery
        dispatcher.registerService(SddReadRequest.class, SDD_ENDPOINT);
        dispatcher.registerService(SddWriteRequest.class, SDD_ENDPOINT);

        // UrlMapping
        dispatcher.registerService(ResolveUrlRequest.class,
                URL_MAPPING_SERVER_ENDPOINT);
        dispatcher.registerService(UrlRegistrationRequest.class,
                URL_MAPPING_SERVER_ENDPOINT);
    }

    private void load() throws ServiceOverloadedException,
            FileNotFoundException {
        // load bands

        BandCsvParser bandParser =
                new BandCsvParser("src/main/resources/Band.csv");
        for (Band band : bandParser) {
            bands.put(band.getLegacyId(), band);
            //            System.out.println("b[" + band.getLegacyId() + "|" + band.getMuid()
            //                    + "] " + band.getName());
        }
        System.out.println(bands.size() + " bands imported");

        // load records
        RecordCsvParser recordParser =
                new RecordCsvParser("src/main/resources/Album.csv");
        for (Record record : recordParser) {
            records.put(record.getLegacyId(), record);
            //            System.out.println("r[" + record.getLegacyId() + "|"
            //                    + record.getMuid() + "]" + record.getName());
        }
        System.out.println(records.size() + " records imported");

        Set<Long> linked = new LinkedHashSet<Long>();

        // link records to band
        Band band;
        Record record;
        BandAlbumCsvParser bandRecordParser =
                new BandAlbumCsvParser("src/main/resources/BandAlbum.csv");
        for (Entry<Long, Long> relation : bandRecordParser) {
            band = bands.get(relation.getValue());
            record = records.get(relation.getKey());

            if (record == null) {
                // sick records waiting for the eXecuT0r
                continue;
            }

            if (band != null) {
                // record has parental band
                band.addRecord(record);
                //            System.out.println(band.getMuid() + " -> " + record.getMuid());
            } else {
                // joint venture
                record.setBand(null);
                //                System.out.println("_ -> " + record.getMuid());
            }
            linked.add(record.getLegacyId());
        }
        System.out.println(linked.size() + " records linked");

        // load tracks
        //        Record parentRecord;
        //        TrackCsvParser trackParser = new TrackCsvParser("tracks.csv");
        //        for (Track track : trackParser) {
        //            tracks.put(track.getLegacyId(), track);
        //
        //            parentRecord = records.get(recordIds.get(track.getRecordName()));
        //            parentRecord.addTrack(track);
        //        }

        // load images
        //        ImageCsvParser imageParser = new ImageCsvParser("images.csv");
        //        for (Image image : imageParser) {
        //            images.put(image.getLegacyId(), image);
        //        }
    }

}
