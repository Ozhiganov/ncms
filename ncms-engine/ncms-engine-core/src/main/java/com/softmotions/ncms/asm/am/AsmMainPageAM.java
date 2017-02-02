package com.softmotions.ncms.asm.am;

import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.softmotions.commons.json.JsonUtils;
import com.softmotions.ncms.asm.AsmAttribute;
import com.softmotions.ncms.asm.AsmAttributeManagerContext;
import com.softmotions.ncms.asm.AsmOptions;
import com.softmotions.ncms.asm.PageRS;
import com.softmotions.ncms.asm.render.AsmRendererContext;
import com.softmotions.ncms.asm.render.AsmRenderingException;

/**
 * Main(index) page marker template attribute.
 *
 * @author Adamansky Anton (adamansky@softmotions.com)
 */

@Singleton
public class AsmMainPageAM extends AsmAttributeManagerSupport {

    private static final Logger log = LoggerFactory.getLogger(AsmMainPageAM.class);

    public static final String[] TYPES = new String[]{"mainpage"};

    private PageRS pageRS;

    private ObjectMapper mapper;

    @Override
    public String[] getSupportedAttributeTypes() {
        return TYPES;
    }

    @Inject
    public AsmMainPageAM(PageRS pageRS, ObjectMapper mapper) {
        this.pageRS = pageRS;
        this.mapper = mapper;
    }

    @Override
    public boolean isUniqueAttribute() {
        return true;
    }

    @Override
    public Object renderAsmAttribute(AsmRendererContext ctx, String attrname, Map<String, String> options) throws AsmRenderingException {
        return null;
    }

    @Override
    public AsmAttribute applyAttributeOptions(AsmAttributeManagerContext ctx, AsmAttribute attr, JsonNode val) throws Exception {
        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        AsmOptions old = new AsmOptions(attr.getOptions());
        AsmOptions opts = new AsmOptions();

        JsonNode newOptions = val.path("options");
        JsonUtils.populateMapByJsonNode((ObjectNode) newOptions, opts, "lang", "enabled", "vhost");
        attr.setOptions(opts.toString());

        JsonNode vals = val.get("value");
        ObjectNode newValues = mapper.createObjectNode();
        newValues.put("robots.txt", vals.path("robots.txt").asText());
        newValues.put("favicon", vals.path("favicon").asText());


        boolean valueChanged;
        String effectiveValue = attr.getEffectiveValue();
        if (effectiveValue != null) {
            JsonNode oldValues = mapper.readTree(effectiveValue);
            valueChanged = !Objects.equals(oldValues.path("robots.txt").asText(), vals.path("robots.txt").asText()) ||
                    !Objects.equals(oldValues.path("favicon").asText(), vals.path("favicon").asText());
        } else {
            valueChanged = true;
        }

        attr.setEffectiveValue(mapper.writeValueAsString(newValues));
        if (!Objects.equals(old.get("lang"), opts.get("lang")) ||
                !Objects.equals(old.get("vhost"), opts.get("vhost")) ||
                !Objects.equals(old.get("enabled"), String.valueOf(opts.get("enabled"))) ||
                valueChanged) {
            ctx.setUserData("reload", Boolean.TRUE);
        }
        return attr;
    }

    @Override
    public AsmAttribute applyAttributeValue(AsmAttributeManagerContext ctx, AsmAttribute attr, JsonNode val) throws Exception {
        return applyAttributeOptions(ctx, attr, val);
    }

    @Override
    public void attributePersisted(AsmAttributeManagerContext ctx, AsmAttribute attr, JsonNode val, JsonNode opts) throws Exception {
        //noinspection ObjectEquality
        if (ctx.getUserData("reload") == Boolean.TRUE) {
            log.info("Trigger index pages reloading");
            pageRS.reloadIndexPages();
        }
    }

    @Override
    public AsmAttribute handleAssemblyCloned(AsmAttributeManagerContext ctx, AsmAttribute attr, Map<Long, Long> fmap) throws Exception {
        //noinspection MismatchedQueryAndUpdateOfCollection
        AsmOptions opts = new AsmOptions(attr.getOptions());
        opts.put("enabled", "false");
        attr.setOptions(opts.toString());
        attr.setEffectiveValue(null);
        return attr;
    }
}
