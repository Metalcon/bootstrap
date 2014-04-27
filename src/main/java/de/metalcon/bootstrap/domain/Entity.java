package de.metalcon.bootstrap.domain;

import java.util.HashMap;
import java.util.Map;

import de.metalcon.domain.UidType;

public abstract class Entity extends UidInstance implements UrlImportable,
        SddImportable {

    public Entity(
            long legacyId,
            UidType uidType,
            String name) {
        super(uidType, legacyId, name);
    }

    protected Map<String, String> getProperties() {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("name", name);
        return properties;
    }

}
