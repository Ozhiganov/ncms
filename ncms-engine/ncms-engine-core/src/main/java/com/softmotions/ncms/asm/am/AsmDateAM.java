package com.softmotions.ncms.asm.am;

import com.softmotions.commons.json.JsonUtils;
import com.softmotions.ncms.asm.Asm;
import com.softmotions.ncms.asm.AsmAttribute;
import com.softmotions.ncms.asm.AsmDAO;
import com.softmotions.ncms.asm.AsmOptions;
import com.softmotions.ncms.asm.render.AsmRendererContext;
import com.softmotions.ncms.asm.render.AsmRenderingException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * @author Adamansky Anton (adamansky@gmail.com)
 */

@Singleton
public class AsmDateAM implements AsmAttributeManager {

    private static final Logger log = LoggerFactory.getLogger(AsmDateAM.class);

    public static final String[] TYPES = new String[]{"date"};

    private final AsmDAO adao;

    @Inject
    public AsmDateAM(AsmDAO adao) {
        this.adao = adao;
    }

    public String[] getSupportedAttributeTypes() {
        return TYPES;
    }

    public AsmAttribute prepareGUIAttribute(Asm page, Asm template, AsmAttribute tmplAttr, AsmAttribute attr) throws Exception {
        return attr;
    }

    // TODO: date for lucene
    public String[] prepareFulltextSearchData(AsmAttribute attr) {
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }

    public Object renderAsmAttribute(AsmRendererContext ctx, String attrname, Map<String, String> options) throws AsmRenderingException {
        Asm asm = ctx.getAsm();
        AsmAttribute attr = asm.getEffectiveAttribute(attrname);
        if (attr == null || attr.getEffectiveValue() == null) {
            return null;
        }
        String val = attr.getEffectiveValue();
        Long ms;
        try {
            ms = Long.parseLong(val);
        } catch (NumberFormatException e) {
            log.error("", e);
            return null;
        }
        return new Date(ms);
    }

    public AsmAttribute applyAttributeOptions(AsmAttributeManagerContext ctx, AsmAttribute attr, JsonNode val) throws Exception {
        AsmOptions asmOpts = new AsmOptions();
        JsonUtils.populateMapByJsonNode((ObjectNode) val, asmOpts, "format");
        attr.setOptions(asmOpts.toString());
        return attr;
    }

    public AsmAttribute applyAttributeValue(AsmAttributeManagerContext ctx, AsmAttribute attr, JsonNode val) throws Exception {
        attr.setEffectiveValue(val.hasNonNull("value") ? val.get("value").asText() : null);
        return attr;
    }

    public void attributePersisted(AsmAttributeManagerContext ctx, AsmAttribute attr, JsonNode val) throws Exception {
        val = val.get("value");
        if (val == null) {
            return;
        }
        long date = val.asLong(0);
        if (date == 0L) {
            return;
        }
        adao.updateAttrsIdxNumberValues(attr, Arrays.asList(date));
    }
}