package de.metalcon.bootstrap.domain;

import java.util.Date;

import de.metalcon.domain.Muid;
import de.metalcon.domain.UidType;

public class Image extends UidInstance {

    protected Date date;

    protected int width;

    protected int height;

    protected Muid entity;

    public Image(
            long legacyId,
            String fileName,
            Date date,
            int width,
            int height) {
        super(UidType.IMAGE, legacyId, fileName);
        this.date = date;
        this.width = width;
        this.height = height;
    }

    public Date getDate() {
        return date;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Muid getEntity() {
        return entity;
    }

    public void setEntity(Muid muid) {
        entity = muid;
    }

}
