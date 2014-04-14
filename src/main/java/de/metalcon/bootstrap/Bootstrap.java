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
import de.metalcon.bootstrap.domain.impl.Band;
import de.metalcon.bootstrap.domain.impl.Record;
import de.metalcon.bootstrap.domain.impl.Track;
import de.metalcon.domain.Muid;
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

    private static ZMQ.Context context;

    private Dispatcher dispatcher;

    private Map<Muid, Band> bands = new HashMap<Muid, Band>();

    private Map<Muid, Record> records = new HashMap<Muid, Record>();

    private Map<Muid, Track> tracks = new HashMap<Muid, Track>();

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
            public void onError(RequestException exception) {
                exception.printStackTrace();
            }

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

        });
    }

    private void registerAdapters(Dispatcher dispatcher) {
        // StaticDataDelivery
        System.out.println("Register SddRequest");
        dispatcher.registerService(SddRequest.class, SDD_ENDPOINT);
        //        ZmqAdapter sddAdapter = new ZmqAdapter(context, SDD_ENDPOINT);
        //        dispatcher.registerServiceAdapter(SDD_SERVICE, sddAdapter);
        //        dispatcher.setDefaultService(SddReadRequest.class, SDD_SERVICE);
        //        dispatcher.setDefaultService(SddWriteRequest.class, SDD_SERVICE);

        // UrlMapping
        System.out.println("Register UrlMappingRequest");
        dispatcher.registerService(UrlMappingRequest.class,
                URL_MAPPING_SERVER_ENDPOINT);
        //        ZmqAdapter urlMappingAdapter =
        //                new ZmqAdapter(context, URL_MAPPING_SERVER_ENDPOINT);
        //        dispatcher.registerServiceAdapter(URL_MAPPING_SERVICE,
        //                urlMappingAdapter);
        //        dispatcher.setDefaultService(UrlMappingResolveRequest.class,
        //                URL_MAPPING_SERVICE);
        //        dispatcher.setDefaultService(UrlMappingRegistrationRequest.class,
        //                URL_MAPPING_SERVICE);
    }

    private void load() throws ServiceOverloadedException {
        //// Ensiferum /////////////////////////////////////////////////////////

        Band ensiferum = new Band("Ensiferum");
        Record ensiferum_ = new Record("Ensiferum", 2000, ensiferum);
        Record fromAfar = new Record("From Afar", 2000, ensiferum);
        Record iron = new Record("Iron", 2000, ensiferum);
        Track heroInADream =
                new Track("Hero in a Dream", 2, ensiferum, ensiferum_);
        Track tokenOfTime =
                new Track("Token of Time", 3, ensiferum, ensiferum_);
        Track guardiansOfFate =
                new Track("Guardians of Fate", 4, ensiferum, ensiferum_);
        Track fromAfar_ = new Track("From Afar", 2, ensiferum, fromAfar);
        Track twilightTavern =
                new Track("Twilight Tavern", 3, ensiferum, fromAfar);
        Track stoneColdMetal =
                new Track("Stone Cold Metal", 6, ensiferum, fromAfar);
        Track iron_ = new Track("Iron", 2, ensiferum, iron);
        Track slayerOfLight = new Track("Slayer of Light", 7, ensiferum, iron);
        Track intoBattle = new Track("Into Battle", 8, ensiferum, iron);

        ensiferum.addRecord(ensiferum_);
        ensiferum.addRecord(fromAfar);
        ensiferum.addRecord(iron);

        ensiferum.addTrack(heroInADream);
        ensiferum.addTrack(tokenOfTime);
        ensiferum.addTrack(guardiansOfFate);
        ensiferum.addTrack(fromAfar_);
        ensiferum.addTrack(twilightTavern);
        ensiferum.addTrack(stoneColdMetal);
        ensiferum.addTrack(iron_);
        ensiferum.addTrack(slayerOfLight);
        ensiferum.addTrack(intoBattle);

        ensiferum_.addTrack(heroInADream);
        ensiferum_.addTrack(tokenOfTime);
        ensiferum_.addTrack(guardiansOfFate);

        fromAfar.addTrack(twilightTavern);
        fromAfar.addTrack(stoneColdMetal);

        iron.addTrack(iron_);
        iron.addTrack(slayerOfLight);
        iron.addTrack(intoBattle);

        store(ensiferum);
        store(ensiferum_);
        store(fromAfar);
        store(iron);
        store(heroInADream);
        store(tokenOfTime);
        store(guardiansOfFate);
        store(fromAfar_);
        store(twilightTavern);
        store(stoneColdMetal);
        store(iron_);
        store(slayerOfLight);
        store(intoBattle);
        store(heroInADream);
        store(tokenOfTime);
        store(guardiansOfFate);
        store(twilightTavern);
        store(stoneColdMetal);
        store(iron_);
        store(slayerOfLight);
        store(intoBattle);

        //// Rammstein /////////////////////////////////////////////////////////

        Band rammstein = new Band("Rammstein");
        Record herzeleid = new Record("Herzeleid", 2000, rammstein);
        Track wolltIhrDasBettInFlammenSehen =
                new Track("Wollt ihr das Bett in Flammen sehen", 1, ensiferum,
                        herzeleid);
        Track derMeister = new Track("Der Meister", 2, ensiferum, herzeleid);
        Track weissesFleisch =
                new Track("Weisses Fleisch", 3, ensiferum, herzeleid);
        Record sehnsucht = new Record("Sehnsucht", 2000, rammstein);
        Track sehnsucht_ = new Track("Sehnsucht", 1, ensiferum, sehnsucht);
        Track engel = new Track("Engel", 2, ensiferum, sehnsucht);
        Track tier = new Track("Tier", 3, ensiferum, sehnsucht);

        rammstein.addRecord(herzeleid);
        rammstein.addRecord(sehnsucht);

        rammstein.addTrack(wolltIhrDasBettInFlammenSehen);
        rammstein.addTrack(derMeister);
        rammstein.addTrack(weissesFleisch);
        rammstein.addTrack(sehnsucht_);
        rammstein.addTrack(engel);
        rammstein.addTrack(tier);

        herzeleid.addTrack(wolltIhrDasBettInFlammenSehen);
        herzeleid.addTrack(derMeister);
        herzeleid.addTrack(weissesFleisch);

        sehnsucht.addTrack(sehnsucht_);
        sehnsucht.addTrack(engel);
        sehnsucht.addTrack(tier);

        store(rammstein);
        store(herzeleid);
        store(sehnsucht);
        store(wolltIhrDasBettInFlammenSehen);
        store(derMeister);
        store(weissesFleisch);
        store(sehnsucht_);
        store(engel);
        store(tier);
        store(wolltIhrDasBettInFlammenSehen);
        store(derMeister);
        store(weissesFleisch);
        store(sehnsucht_);
        store(engel);
        store(tier);

        //// Metallica /////////////////////////////////////////////////////////

        Band metallica = new Band("Metallica");
        Record killEmAll = new Record("Kill 'em All", 2000, metallica);
        Track hitTheLights =
                new Track("Hit the Lights", 1, ensiferum, killEmAll);
        Track theFourHorsemen =
                new Track("The Four Horsemen", 2, ensiferum, killEmAll);
        Record rideTheLightning =
                new Record("Ride the Lightning", 2000, metallica);
        Track fightFireWithFire =
                new Track("Fight Fire With Fire", 1, ensiferum,
                        rideTheLightning);
        Track rideTheLightning_ =
                new Track("Ride the Lightning", 2, ensiferum, rideTheLightning);
        Track forWhomTheBellTolls =
                new Track("For Whom the Bell Tolls", 3, ensiferum,
                        rideTheLightning);

        metallica.addRecord(killEmAll);
        metallica.addRecord(rideTheLightning);

        metallica.addTrack(hitTheLights);
        metallica.addTrack(theFourHorsemen);
        metallica.addTrack(rideTheLightning_);
        metallica.addTrack(fightFireWithFire);
        metallica.addTrack(rideTheLightning_);
        metallica.addTrack(forWhomTheBellTolls);

        killEmAll.addTrack(hitTheLights);
        killEmAll.addTrack(theFourHorsemen);

        rideTheLightning.addTrack(fightFireWithFire);
        rideTheLightning.addTrack(rideTheLightning_);
        rideTheLightning.addTrack(forWhomTheBellTolls);

        store(metallica);
        store(killEmAll);
        store(rideTheLightning);
        store(hitTheLights);
        store(theFourHorsemen);
        store(rideTheLightning_);
        store(fightFireWithFire);
        store(rideTheLightning_);
        store(forWhomTheBellTolls);
        store(hitTheLights);
        store(theFourHorsemen);
        store(fightFireWithFire);
        store(rideTheLightning_);
        store(forWhomTheBellTolls);
    }

    private void store(Band band) {
        bands.put(band.getMuid(), band);
    }

    private void store(Record record) {
        records.put(record.getMuid(), record);
    }

    private void store(Track track) {
        tracks.put(track.getMuid(), track);
    }

}
