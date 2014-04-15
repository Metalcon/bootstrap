package de.metalcon.bootstrap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.hh.request_dispatcher.Callback;
import net.hh.request_dispatcher.Dispatcher;
import net.hh.request_dispatcher.RequestException;
import de.metalcon.api.responses.Response;
import de.metalcon.api.responses.SuccessResponse;
import de.metalcon.api.responses.errors.ErrorResponse;
import de.metalcon.bootstrap.domain.Disc;
import de.metalcon.bootstrap.domain.Entity;
import de.metalcon.bootstrap.domain.impl.Band;
import de.metalcon.bootstrap.domain.impl.Record;
import de.metalcon.bootstrap.domain.impl.Track;
import de.metalcon.bootstrap.parsers.BandAlbumCsvParser;
import de.metalcon.bootstrap.parsers.BandCsvParser;
import de.metalcon.bootstrap.parsers.DiscCsvParser;
import de.metalcon.bootstrap.parsers.RecordCsvParser;
import de.metalcon.bootstrap.parsers.TrackCsvParser;
import de.metalcon.exceptions.ServiceOverloadedException;
import de.metalcon.sdd.api.requests.SddRequest;
import de.metalcon.sdd.api.requests.SddWriteRequest;
import de.metalcon.sdd.api.responses.SddResponse;
import de.metalcon.sdd.api.responses.SddSucessfullQueueResponse;
import de.metalcon.urlmappingserver.api.requests.UrlMappingRequest;
import de.metalcon.urlmappingserver.api.requests.UrlRegistrationRequest;

public class Bootstrap {

    public static final String SDD_SERVICE = "staticDataDeliveryServer";

    public static final String SDD_ENDPOINT = "tcp://127.0.0.1:1337";

    public static final String URL_MAPPING_SERVICE = "urlMappingServer";

    public static final String URL_MAPPING_SERVER_ENDPOINT =
            "tcp://127.0.0.1:12666";

    private Dispatcher dispatcher;

    private Map<Long, Band> bands;

    private Map<Long, Record> records;

    private Map<Long, Track> tracks;

    private Map<Long, Disc> discs;

    public static void main(String[] args) throws ServiceOverloadedException,
            InterruptedException, IOException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.load(1000);
        bootstrap.run();
    }

    private void run() throws ServiceOverloadedException, InterruptedException,
            IOException {
        dispatcher = new Dispatcher();
        registerAdapters(dispatcher);

        SddWriteRequest sddWriteRequest = new SddWriteRequest();

        // FIXME make sure to send 1000 requests only
        Band testy = bands.get(377L);
        System.out.println("Testy: \"" + testy.getName() + "\"");
        for (Band band : bands.values()) {
            registerUrl(band);
            band.fillSddWriteRequest(sddWriteRequest);
            importToStaticData(sddWriteRequest);
        }
        dispatcher.gatherResults(1000);

        for (Record record : records.values()) {
            record.fillSddWriteRequest(sddWriteRequest);
            registerUrl(record);
            importToStaticData(sddWriteRequest);

            if (record.getBands().contains(testy)) {
                System.out.println("record: \"" + record.getName() + "\"");
            }
        }
        dispatcher.gatherResults(1000);
        for (Track track : tracks.values()) {
            track.fillSddWriteRequest(sddWriteRequest);
            registerUrl(track);
        }
        dispatcher.gatherResults(2000);

        System.out.println("done.");
        dispatcher.shutdown();
    }

    protected void importToStaticData(SddWriteRequest request) {
        dispatcher.execute(request, new Callback<SddResponse>() {

            @Override
            public void onError(RequestException exception) {
                exception.printStackTrace();
            }

            @Override
            public void onSuccess(SddResponse response) {
                if (response instanceof SddSucessfullQueueResponse) {
                    //                    System.out.println("import successfully done");
                } else {
                    System.out.println("Queing data failed: "
                            + response.getClass());
                }
            }

        });
    }

    private void registerUrl(final Entity entity) {
        UrlRegistrationRequest urlRequest =
                new UrlRegistrationRequest(entity.getUrlData());
        dispatcher.execute(urlRequest, new Callback<Response>() {

            @Override
            public void onSuccess(Response response) {
                if (response instanceof SuccessResponse) {
                    //                    System.out.println("Url registration worked. ("
                    //                            + entity.getClass().getSimpleName() + ":"
                    //                            + entity.getName() + ")");
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

            @Override
            public void onTimeout() {
                System.err.println("request timed out");
            }

        });
    }

    private void registerAdapters(Dispatcher dispatcher) {
        // StaticDataDelivery
        dispatcher.registerService(SddRequest.class, SDD_ENDPOINT);

        // UrlMapping
        dispatcher.registerService(UrlMappingRequest.class,
                URL_MAPPING_SERVER_ENDPOINT);
    }

    private void load(int numEntities) throws ServiceOverloadedException,
            FileNotFoundException {
        String csvDir = "src/main/resources/";

        // load bands
        bands = loadBands(csvDir + "Band.csv");
        System.out.println(bands.size() + " bands imported");

        // load records
        records = loadRecords(csvDir + "Album.csv");
        System.out.println(records.size() + " records imported");

        // link records to bands
        Set<Long> unusedRecords =
                linkRecords(csvDir + "BandAlbum.csv", bands, records);

        // remove unused records
        removeUnusedRecords(unusedRecords);
        System.out.println(records.size() + " records linked");

        // load tracks
        tracks = loadTracks(csvDir + "Track.csv");
        System.out.println(tracks.size() + " tracks imported");

        // load discs
        discs = loadDiscs(csvDir + "Disc.csv");
        System.out.println(discs.size() + " discs imported");

        // link tracks to records
        Set<Long> unusedTracks = linkTracks(bands, records, discs, tracks);

        // remove unused tracks
        removeUnusedTracks(unusedTracks);
        System.out.println(tracks.size() + " tracks linked");

        // append filter
        cutToNumEntities(numEntities);
        System.out.println("cutted down to " + bands.size() + " bands,");
        System.out.println(records.size() + " records,");
        System.out.println(tracks.size() + " tracks");
    }

    protected void removeUnusedRecords(Set<Long> unusedRecords) {
        Record record;
        for (Long recordId : unusedRecords) {
            record = records.get(recordId);
            records.remove(recordId);
            //            System.out.println("r[" + record.getLegacyId() + "|"
            //                    + record.getMuid() + "] \"" + record.getName()
            //                    + "\" trashed");
        }
        //        unusedRecords.clear();
    }

    protected void removeUnusedTracks(Set<Long> unusedTracks) {
        Track track;
        for (Long trackId : unusedTracks) {
            track = tracks.get(trackId);
            tracks.remove(trackId);
            //            System.out
            //                    .println("t[" + track.getLegacyId() + "|" + track.getMuid()
            //                            + "] \"" + track.getName() + "\" trashed");
        }
        //        unusedTracks.clear();
    }

    protected void cutToNumEntities(int numEntities) {
        int crrEntities = 0;
        List<Long> unusedBands = new LinkedList<Long>();
        for (Band crrBand : bands.values()) {
            if (++crrEntities > numEntities) {
                unusedBands.add(crrBand.getLegacyId());

                // remove children
                for (Record crrRecord : crrBand.getRecords()) {
                    records.remove(crrRecord.getLegacyId());
                }
                for (Track crrTrack : crrBand.getTracks()) {
                    tracks.remove(crrTrack.getLegacyId());
                }
            }
        }
        for (Long unusedBandId : unusedBands) {
            bands.remove(unusedBandId);
        }
    }

    protected static Map<Long, Band> loadBands(String filePath)
            throws FileNotFoundException {
        Map<Long, Band> bands = new HashMap<Long, Band>();
        BandCsvParser bandParser = new BandCsvParser(filePath);
        for (Band band : bandParser) {
            bands.put(band.getLegacyId(), band);
            //            System.out.println("b[" + band.getLegacyId() + "|" + band.getMuid()
            //                    + "] " + band.getName());
        }
        return bands;
    }

    protected static Map<Long, Record> loadRecords(String filePath)
            throws FileNotFoundException {
        Map<Long, Record> records = new HashMap<Long, Record>();
        RecordCsvParser recordParser = new RecordCsvParser(filePath);
        for (Record record : recordParser) {
            records.put(record.getLegacyId(), record);
            //            System.out.println("r[" + record.getLegacyId() + "|"
            //                    + record.getMuid() + "] " + record.getName());
        }
        return records;
    }

    protected static Map<Long, Track> loadTracks(String filePath)
            throws FileNotFoundException {
        Map<Long, Track> tracks = new HashMap<Long, Track>();
        TrackCsvParser trackParser = new TrackCsvParser(filePath);
        for (Track track : trackParser) {
            tracks.put(track.getLegacyId(), track);
            //            System.out.println("t[" + track.getLegacyId() + "|"
            //                    + track.getMuid() + "] " + track.getName());
        }
        return tracks;
    }

    protected static Map<Long, Disc> loadDiscs(String filePath)
            throws FileNotFoundException {
        Map<Long, Disc> discs = new HashMap<Long, Disc>();
        DiscCsvParser discParser = new DiscCsvParser(filePath);
        for (Disc disc : discParser) {
            discs.put(disc.getLegacyId(), disc);
            //            System.out.println("d[" + disc.getLegacyId() + "] "
            //                    + disc.getTitle());
        }
        return discs;
    }

    protected static Set<Long> linkRecords(
            String filePath,
            Map<Long, Band> bands,
            Map<Long, Record> records) throws FileNotFoundException {
        Band band;
        Record record;
        Set<Long> unusedRecords = new LinkedHashSet<Long>(records.keySet());
        BandAlbumCsvParser bandRecordParser = new BandAlbumCsvParser(filePath);
        for (Entry<Long, List<Long>> relation : bandRecordParser) {
            record = records.get(relation.getKey());
            if (record == null) {
                // sick relations waiting for the eXecuT0r
                continue;
            }

            for (Long bandId : relation.getValue()) {
                // add parental band
                band = bands.get(bandId);
                if (band == null) {
                    throw new IllegalStateException("band null: " + bandId);
                }

                band.addRecord(record);
                //              System.out.println(band.getMuid() + " -> " + record.getMuid());
            }

            unusedRecords.remove(record.getLegacyId());
        }
        return unusedRecords;
    }

    protected static Set<Long> linkTracks(
            Map<Long, Band> bands,
            Map<Long, Record> records,
            Map<Long, Disc> discs,
            Map<Long, Track> tracks) {
        Record record;
        Disc disc;
        Track track;
        Set<Long> unusedTracks = new LinkedHashSet<Long>(tracks.keySet());
        for (Long trackId : tracks.keySet()) {
            track = tracks.get(trackId);
            disc = discs.get(track.getDiscId());

            record = records.get(disc.getRecordId());
            if (record == null) {
                continue;
            }

            record.addTrack(track);
            for (Band band : record.getBands()) {
                band.addTrack(track);
            }

            unusedTracks.remove(trackId);
        }
        return unusedTracks;
    }

}
