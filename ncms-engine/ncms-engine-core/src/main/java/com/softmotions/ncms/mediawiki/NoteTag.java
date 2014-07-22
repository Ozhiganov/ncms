package com.softmotions.ncms.mediawiki;

import info.bliki.wiki.filter.Encoder;
import info.bliki.wiki.filter.ITextConverter;
import info.bliki.wiki.model.IWikiModel;
import info.bliki.wiki.tags.HTMLTag;
import info.bliki.wiki.tags.util.INoBodyParsingTag;

import com.google.inject.Singleton;

import java.io.IOException;

/**
 * Simple note tag.
 *
 * @author Adamansky Anton (adamansky@gmail.com)
 */
@Singleton
public class NoteTag extends HTMLTag implements INoBodyParsingTag {

    public NoteTag() {
        super("div");
    }

    public void renderHTML(ITextConverter converter, Appendable buf, IWikiModel model) throws IOException {
        String body = this.getBodyString();
        buf.append("<div class='wiki-note-wrap'>");
        buf.append("<div class='wiki-note'>");
        buf.append(Encoder.encodeHtml(body));
        buf.append("</div>");
        buf.append("</div>");
    }
}