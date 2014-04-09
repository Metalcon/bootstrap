package de.metalcon.bootstrap;

import java.util.HashMap;
import java.util.Map;

import net.hh.request_dispatcher.Callback;
import net.hh.request_dispatcher.Dispatcher;
import net.hh.request_dispatcher.service_adapter.ZmqAdapter;

import org.zeromq.ZMQ;

import de.metalcon.api.responses.Response;
import de.metalcon.domain.Muid;
import de.metalcon.domain.UidType;
import de.metalcon.exceptions.ServiceOverloadedException;
import de.metalcon.sdd.api.requests.SddReadRequest;
import de.metalcon.sdd.api.requests.SddRequest;
import de.metalcon.sdd.api.requests.SddWriteRequest;
import de.metalcon.urlmappingserver.api.requests.UrlMappingRegistrationRequest;
import de.metalcon.urlmappingserver.api.requests.UrlMappingRequest;
import de.metalcon.urlmappingserver.api.requests.UrlMappingResolveRequest;

public class Bootstrap {

    public static final String SDD_SERVICE = "staticDataDeliveryServer";

    public static final String SDD_ENDPOINT = "tcp://127.0.0.1:1337";

    public static final String URL_MAPPING_SERVICE = "urlMappingServer";

    public static final String URL_MAPPING_SERVER_ENDPOINT =
            "tcp://127.0.0.1:12666";

    private static ZMQ.Context context;

    public static void main(String[] args) throws ServiceOverloadedException,
            InterruptedException {
        context = ZMQ.context(1);

        new Bootstrap().run();

        context.close();
    }

    private void run() throws ServiceOverloadedException, InterruptedException {
        Dispatcher dispatcher = new Dispatcher();
        registerAdapters(dispatcher);

        Muid bandMuid = Muid.create(UidType.BAND);

        SddWriteRequest writeRequest = new SddWriteRequest();

        Map<String, String> properties = new HashMap<String, String>();
        properties.put("name", "Ensiferum");
        writeRequest.setProperties(bandMuid, properties);
        dispatcher.execute(writeRequest, new Callback<Response>() {

            @Override
            public void onSuccess(Response arg0) {
            }

        });
        dispatcher.gatherResults();

        dispatcher.close();
    }

    private void registerAdapters(Dispatcher dispatcher) {
        // StaticDataDelivery
        ZmqAdapter<SddRequest, Response> sddAdapter =
                new ZmqAdapter<SddRequest, Response>(context, SDD_ENDPOINT);
        dispatcher.registerServiceAdapter(SDD_SERVICE, sddAdapter);
        dispatcher.setDefaultService(SddReadRequest.class, SDD_SERVICE);
        dispatcher.setDefaultService(SddWriteRequest.class, SDD_SERVICE);

        // UrlMapping
        ZmqAdapter<UrlMappingRequest, Response> urlMappingAdapter =
                new ZmqAdapter<UrlMappingRequest, Response>(context,
                        URL_MAPPING_SERVER_ENDPOINT);
        dispatcher.registerServiceAdapter(URL_MAPPING_SERVICE,
                urlMappingAdapter);
        dispatcher.setDefaultService(UrlMappingResolveRequest.class,
                URL_MAPPING_SERVICE);
        dispatcher.setDefaultService(UrlMappingRegistrationRequest.class,
                URL_MAPPING_SERVICE);
    }

}
