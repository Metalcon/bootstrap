package de.metalcon.bootstrap.domain;

import de.metalcon.domain.UidType;

public abstract class Entity extends UidInstance implements UrlImportable,
        SddImportable {

    public Entity(
            long legacyId,
            UidType uidType,
            String name) {
        super(uidType, legacyId, name);
    }

}
