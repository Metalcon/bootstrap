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
import de.metalcon.bootstrap.domain.Image;
import de.metalcon.bootstrap.domain.entities.Band;
import de.metalcon.bootstrap.domain.entities.Record;
import de.metalcon.bootstrap.domain.entities.Track;
import de.metalcon.bootstrap.parsers.BandAlbumCsvParser;
import de.metalcon.bootstrap.parsers.BandCsvParser;
import de.metalcon.bootstrap.parsers.DiscCsvParser;
import de.metalcon.bootstrap.parsers.ImageCsvParser;
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

    public static final String SERVER = "tcp://141.26.71.69:";

    public static final String SDD_SERVICE = "staticDataDeliveryServer";

    public static final String SDD_ENDPOINT = SERVER + "1337";

    public static final String URL_MAPPING_SERVICE = "urlMappingServer";

    public static final String URL_MAPPING_SERVER_ENDPOINT = SERVER + "12666";

    public static final String IMAGE_GALLERY_SERVICE = "galleryServer";

    public static final String IMAGE_GALLERY_SERVER_ENDPOINT = SERVER + "12668";

    private Dispatcher dispatcher;

    private Map<Long, Band> bands;

    private Map<Long, Record> records;

    private Map<Long, Track> tracks;

    private Map<Long, Disc> discs;

    private Map<Long, Image> images;

    private SddWriteRequest request;

    private int numRequests;

    private int maxRequests;

    private int numActionsSddRequest;

    public static void main(String[] args) throws ServiceOverloadedException,
            InterruptedException, IOException {
        MuidLoader muidLoader = new MuidLoader("muids.csv");
        Entity.setMuidLoader(muidLoader);

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.load(100000);
        //        bootstrap.run();

        //        LastFMAlbumApi lfmApi = new LastFMAlbumApi();
        //        YoutubeApiClient youtubeApiClient = new YoutubeApiClient();
        //        // TODO: we also need the metawebID (for youtube later on.)
        //        Album iron = lfmApi.getTracksByName("ensiferum", "iron");
        //        for (lastFMAlbum.Track t : iron.getTracks()) {
        //            // this call will not make sense since we don't have a topic ID and Mbid won't help
        //            youtubeApiClient.youtubeSongSearch(5, t.getName(), iron.getMbid());
        //            // this call makes more sense but it would have been nice to use the mbid for higher precision
        //            youtubeApiClient.youtubeSongSearch(5, "ensiferum " + t.getName());
        //        }

        muidLoader.store();
    }

    public Bootstrap() throws IOException {
        numRequests = 0;
        maxRequests = 500;
        numActionsSddRequest = 0;
    }

    private void run() throws ServiceOverloadedException, InterruptedException,
            IOException {
        dispatcher = new Dispatcher();
        registerAdapters(dispatcher);

        Band testy = null;

        for (Band band : bands.values()) {
            if (Character.isDigit(band.getName().toCharArray()[0])
                    || band.getName().contains("\\")) {
                continue;
            }

            if (testy == null && band.getRecords().size() == 0) {
                testy = band;
                System.out.println("Testy band is \"" + band.getName() + "\"");
            }

            importEntity(band);
        }

        for (Record record : records.values()) {
            if (record.getBands().size() == 1
                    && record.getBands().contains(testy)) {
                System.out.println("record: \"" + record.getName() + "\"");
            }
            importEntity(record);
        }

        for (Track track : tracks.values()) {
            if (track.getRecord().getBands().size() == 1
                    && track.getRecord().getBands().contains(testy)) {
                System.out.println("track: \"" + track.getName() + "\"");
            }
            importEntity(track);
        }

        dispatcher.gatherResults(1000);
        System.out.println("results gathered");

        System.out.println("done.");
        dispatcher.shutdown();
    }

    protected void importEntity(Entity entity) {
        if (request == null) {
            request = new SddWriteRequest();
        }

        registerUrl(entity);
        entity.fillSddWriteRequest(request);
        numActionsSddRequest += 1;

        // TODO if
        importToStaticData(request);
        request = new SddWriteRequest();
        numActionsSddRequest = 0;
        numRequests += 1;

        if (numRequests >= maxRequests) {
            numRequests = 0;
            dispatcher.gatherResults(5000);
            System.out.println("results gathered");
        }
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
                    //                            System.out.println("Queing data worked");
                } else {
                    System.out.println("Queing data failed: "
                            + response.getClass());
                }
            }

            @Override
            public void onTimeout() {
                System.err.println("static data request timed out");
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
                    //                            + entity.getName() + ")" + entity.getMuid());
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
                System.err.println("URL mapping request timed out");
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

        // load images
        images = loadImages(csvDir + "Image.csv");
        System.out.println(images.size() + " images imported");

        // link images
        Set<Long> unusedImages = linkImages(bands, records, tracks, images);

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

    protected static Map<Long, Image> loadImages(String filePath)
            throws FileNotFoundException {
        Map<Long, Image> images = new HashMap<Long, Image>();
        ImageCsvParser imageParser = new ImageCsvParser(filePath);
        for (Image image : imageParser) {
            images.put(image.getLegacyId(), image);
            //            System.out.println("i[" + image.getLegacyId() + "|"
            //                    + image.getMuid() + "] " + image.getName());
        }
        return images;
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

    protected static Set<Long> linkImages(
            Map<Long, Band> bands,
            Map<Long, Record> records,
            Map<Long, Track> tracks,
            Map<Long, Image> images) {
        Set<Long> unusedImages = new LinkedHashSet<Long>();

        Image image;

        Band band;
        for (Long bandId : bands.keySet()) {
            band = bands.get(bandId);
            image = images.get(band.getPhotoId());
            //            System.out.println(image.getName() + "@" + band.getName());

            image.setEntity(band.getMuid());
        }

        return unusedImages;
    }

}
