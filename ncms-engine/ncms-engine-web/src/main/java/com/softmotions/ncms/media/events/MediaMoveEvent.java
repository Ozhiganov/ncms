package com.softmotions.ncms.media.events;

import com.softmotions.ncms.events.BasicEvent;

import static com.softmotions.ncms.media.MediaRS.normalizeFolder;
import static com.softmotions.ncms.media.MediaRS.normalizePath;


/**
 * Fired if media item was moved.
 *
 * @author Adamansky Anton (adamansky@gmail.com)
 */
public class MediaMoveEvent extends BasicEvent {

    final Long id;

    final boolean isFolder;

    final String oldPath;

    final String newPath;

    public MediaMoveEvent(Object source,
                          Number id, boolean isFolder,
                          String oldPath, String newPath) {
        super(source);
        this.id = id != null ? id.longValue() : null;
        this.oldPath = isFolder ? normalizeFolder(oldPath) : normalizePath(oldPath);
        this.newPath = isFolder ? normalizeFolder(newPath) : normalizePath(newPath);
        this.isFolder = isFolder;
    }

    public Long getId() {
        return id;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public String getOldPath() {
        return oldPath;
    }

    public String getNewPath() {
        return newPath;
    }
}