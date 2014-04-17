package de.metalcon.bootstrap.domain;

import de.metalcon.urlmappingserver.api.requests.registration.EntityUrlData;

public interface UrlImportable {

    String getName();

    EntityUrlData getUrlData();

}
