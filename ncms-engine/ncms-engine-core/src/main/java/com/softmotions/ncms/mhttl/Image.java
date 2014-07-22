package com.softmotions.ncms.mhttl;

/**
* @author Adamansky Anton (adamansky@gmail.com)
*/
public final class Image {

    private long id;
    private boolean restrict;
    private boolean resize;
    private boolean skipSmall;
    private Integer optionsWidth;
    private Integer optionsHeight;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isRestrict() {
        return restrict;
    }

    public void setRestrict(boolean restrict) {
        this.restrict = restrict;
    }

    public boolean isResize() {
        return resize;
    }

    public void setResize(boolean resize) {
        this.resize = resize;
    }

    public boolean isSkipSmall() {
        return skipSmall;
    }

    public void setSkipSmall(boolean skipSmall) {
        this.skipSmall = skipSmall;
    }

    public Integer getOptionsWidth() {
        return optionsWidth;
    }

    public void setOptionsWidth(Integer optionsWidth) {
        this.optionsWidth = optionsWidth;
    }

    public Integer getOptionsHeight() {
        return optionsHeight;
    }

    public void setOptionsHeight(Integer optionsHeight) {
        this.optionsHeight = optionsHeight;
    }

    public String getLink() {
        //todo !!!!
        return "" + id;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder("AsmImageNode{");
        sb.append("id=").append(id);
        sb.append(", optionsWidth=").append(optionsWidth);
        sb.append(", optionsHeight=").append(optionsHeight);
        sb.append(", restrict=").append(restrict);
        sb.append(", resize=").append(resize);
        sb.append(", skipSmall=").append(skipSmall);
        sb.append('}');
        return sb.toString();
    }
}