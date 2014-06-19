/**
 * Select box/list attribute controller
 */
qx.Class.define("ncms.asm.am.SelectAM", {
    extend : qx.core.Object,
    implement : [ ncms.asm.IAsmAttributeManager ],
    include : [ qx.locale.MTranslation, ncms.asm.am.MAttributeManager ],


    statics : {

        getDescription : function() {
            return qx.locale.Manager.tr("Select box");
        },

        getSupportedAttributeTypes : function() {
            return [ "select" ];
        }
    },

    members : {

        _form : null,

        activateOptionsWidget : function(attrSpec, asmSpec) {
            var form = new qx.ui.form.Form();

            //---------- Options
            var opts = ncms.Utils.parseOptions(attrSpec["options"]);
            var el = new qx.ui.form.RadioButtonGroup(new qx.ui.layout.HBox(4));
            el.add(new qx.ui.form.RadioButton(this.tr("table")).set({"model" : "table"}));
            el.add(new qx.ui.form.RadioButton(this.tr("selectbox")).set({"model" : "selectbox"}));
            el.setModelSelection(opts["display"] ? [opts["display"]] : ["selectbox"]);
            form.add(el, this.tr("Display as"), null, "display");

            el = new qx.ui.form.CheckBox();
            form.add(el, this.tr("Multi select"), null, "multiselect");

            //---------- Table
            var table = new ncms.asm.am.SelectAMTable();
            form.add(table, this.tr("Items"), null, "items");
            this._form = form;
            return new sm.ui.form.FlexFormRenderer(form);
        },

        optionsAsJSON : function() {
            return {};
        },

        activateValueEditorWidget : function(attrSpec, asmSpec) {
            return new qx.ui.core.Widget();
        },

        valueAsJSON : function() {
            return {};
        }
    },

    destruct : function() {
        this._disposeObjects("_form");
    }
});