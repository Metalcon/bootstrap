package de.metalcon.bootstrap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.hh.request_dispatcher.Callback;
import net.hh.request_dispatcher.Dispatcher;
import net.hh.request_dispatcher.RequestException;

import org.zeromq.ZMQ;

import de.metalcon.api.responses.Response;
import de.metalcon.api.responses.SuccessResponse;
import de.metalcon.api.responses.errors.ErrorResponse;
import de.metalcon.bootstrap.domain.Entity;
import de.metalcon.bootstrap.domain.Image;
import de.metalcon.bootstrap.domain.impl.Band;
import de.metalcon.bootstrap.domain.impl.Record;
import de.metalcon.bootstrap.domain.impl.Track;
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

    private static ZMQ.Context context;

    private Dispatcher dispatcher;

    private Map<Long, Band> bands = new HashMap<Long, Band>();

    private Map<Long, Record> records = new HashMap<Long, Record>();

    private Map<String, Long> recordIds = new HashMap<String, Long>();

    private Map<Long, Track> tracks = new HashMap<Long, Track>();

    private Map<Long, Image> images = new HashMap<Long, Image>();

    public static void main(String[] args) throws ServiceOverloadedException,
            InterruptedException, IOException {
        context = ZMQ.context(1);

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.load();
        bootstrap.run();

        context.close();
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

    private void load() throws ServiceOverloadedException {
        // load bands
        BandCsvParser bandParser = new BandCsvParser("bands.csv");
        for (Band band : bandParser) {
            bands.put(band.getLegacyId(), band);
        }

        // load records
        Band parent;
        RecordCsvParser recordParser = new RecordCsvParser("records.csv");
        for (Record record : recordParser) {
            records.put(record.getLegacyId(), record);
            recordIds.put(record.getName(), record.getLegacyId());

            parent = bands.get(record.getBandId());
            parent.addRecord(record);
        }

        // load tracks
        Record parentRecord;
        TrackCsvParser trackParser = new TrackCsvParser("tracks.csv");
        for (Track track : trackParser) {
            tracks.put(track.getLegacyId(), track);

            parentRecord = records.get(recordIds.get(track.getRecordName()));
            parentRecord.addTrack(track);
        }

        // load images
        ImageCsvParser imageParser = new ImageCsvParser("images.csv");
        for (Image image : imageParser) {
            images.put(image.getLegacyId(), image);
        }
    }

}
