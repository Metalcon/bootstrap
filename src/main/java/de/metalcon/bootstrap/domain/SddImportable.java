package de.metalcon.bootstrap.domain;

import de.metalcon.sdd.api.requests.SddWriteRequest;

public interface SddImportable {

    public abstract void fillSddWriteRequest(SddWriteRequest request);

}
