package de.metalcon.bootstrap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.metalcon.domain.Muid;
import de.metalcon.domain.UidType;
import de.metalcon.testing.MuidFactory;

public class MuidLoader {

    private String filePath;

    private Map<UidType, Map<Long, Muid>> muids;

    private Set<Muid> realMuids;

    public MuidLoader(
            String filePath) throws IOException {
        this.filePath = filePath;
        muids = new HashMap<UidType, Map<Long, Muid>>();
        realMuids = new LinkedHashSet<Muid>();

        File persFile = new File(filePath);
        if (!persFile.exists()) {
            return;
        }

        BufferedReader reader = new BufferedReader(new FileReader(persFile));
        String line;
        Map<Long, Muid> muidsForUidType;

        UidType uidType;
        Long legacyId;
        Muid muid;

        while ((line = reader.readLine()) != null) {
            String[] entry = line.split(",");

            // [0] UidType
            uidType = UidType.parseString(entry[0]);
            // [1] legacy ID
            legacyId = Long.parseLong(entry[1]);
            // [2] MUID
            muid = Muid.createFromID(entry[2]);

            muidsForUidType = muids.get(uidType);
            if (muidsForUidType == null) {
                muidsForUidType = new HashMap<Long, Muid>();
                muids.put(uidType, muidsForUidType);
            }

            if (muidsForUidType.containsKey(legacyId)) {
                throw new IllegalStateException("legacy ID \"" + legacyId
                        + "\" already registered for UID type \"" + uidType
                        + "\"");
            }
            if (realMuids.contains(muid)) {
                throw new IllegalStateException("MUID \"" + muid
                        + "\" already taken");
            }
            muidsForUidType.put(legacyId, muid);
            realMuids.add(muid);
        }
    }

    public void store() throws IOException {
        PrintWriter writer = new PrintWriter(filePath);

        try {
            Map<Long, Muid> muidsForUidType;
            for (UidType uidType : muids.keySet()) {
                muidsForUidType = muids.get(uidType);
                for (Entry<Long, Muid> entry : muidsForUidType.entrySet()) {
                    writer.println(uidType + ","
                            + String.valueOf(entry.getKey()) + ","
                            + entry.getValue());
                }
            }
            writer.flush();
        } finally {
            writer.close();
        }
    }

    public Muid getMuid(Long legacyId, UidType uidType) {
        Map<Long, Muid> muidsForUidType = muids.get(uidType);

        if (muidsForUidType != null) {
            // search for MUID
            if (muidsForUidType.containsKey(legacyId)) {
                return muidsForUidType.get(legacyId);
            }
        } else {
            // create empty map for current entity type
            muidsForUidType = new HashMap<Long, Muid>();
            muids.put(uidType, muidsForUidType);
        }

        // create new MUID
        Muid muid = MuidFactory.generateMuid(uidType);
        muidsForUidType.put(legacyId, muid);
        return muid;
    }

}
