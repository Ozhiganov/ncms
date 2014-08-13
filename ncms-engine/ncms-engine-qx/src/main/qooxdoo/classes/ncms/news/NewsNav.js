/**
 * News navigator
 *
 * @asset(ncms/icon/16/misc/chain.png)
 */
qx.Class.define("ncms.news.NewsNav", {
    extend : qx.ui.core.Widget,

    statics : {

        NEWS_EDITOR_CLAZZ : "ncms.news.NewsEditor"
    },

    events : {
    },

    properties : {
    },

    construct : function() {
        this.base(arguments);
        this._setLayout(new qx.ui.layout.VBox(4));
        this.setPadding([0, 0, 0, 10]);

        //Register page editor
        var eclazz = ncms.news.NewsNav.NEWS_EDITOR_CLAZZ;
        var app = ncms.Application.INSTANCE;
        app.registerWSA(eclazz, function() {
            return new ncms.news.NewsEditor();
        }, null, this);

        this.addListener("disappear", function() {
            //Navigation side is inactive so hide page editor pane if it not done already
            if (app.getActiveWSAID() == eclazz) {
                app.showDefaultWSA();
            }
        }, this);
        this.addListener("appear", function() {
            if (app.getActiveWSAID() != eclazz) {
                if (this.getSelectedPage() != null) {
                    app.showWSA(eclazz);
                } else {
                    app.showDefaultWSA();
                }
            }
        }, this);

        var bf = this.__bf = new sm.ui.form.ButtonField(null, "ncms/icon/16/misc/chain.png");
        bf.addListener("execute", this.__choosePage);
        bf.setPlaceholder(this.tr("Select the parent page for news"));
        this._add(bf);

        this.__ps = new ncms.pgs.PagesSearchSelector({
            "type" : "page.news"
        }).set({searchIfEmpty : true});
        this._add(this.__ps, {flex : 1});

        this.__ps.addListener("itemSelected", function(ev) {
            var data = ev.getData();
            if (data == null) {
                app.showDefaultWSA();
            } else {
                app.getWSA(eclazz).setPageSpec({
                    id : data["id"],
                    name : data["label"]
                });
                app.showWSA(eclazz);
            }
        }, this);

        this.__syncState();
    },

    members : {

        /**
         * Linked pages bf
         */
        __bf : null,

        /**
         * Pages selector
         */
        __ps : null,

        getSelectedPage : function() {
            var sp = this.__ps.getSelectedPage();
            return (sp == null) ? null : {id : sp["id"], name : sp["label"]};
        },

        __choosePage : function() {
            var dlg = new ncms.pgs.PagesCollectionDlg(this.tr("Please select the parent page for news"),
                    {
                        type : "pages.news.roots",
                        accessAll : "n" //Required news editing access rights
                    });
            dlg.addListener("completed", function(ev) {
                var data = ev.getData();
                qx.log.Logger.info("News root changed! data=" + JSON.stringify(data));
                dlg.close();
            }, this);
            dlg.open();
        },

        __syncState : function() {
            var page = this.__bf.getUserData("page");
            if (page == null) {
                this.__ps.setEnabled(false);
                ncms.Application.INSTANCE.showDefaultWSA();
                this.__bf.getMainButton().addState("invalid");
            } else {
                this.__ps.setEnabled(true);
                this.__bf.getMainButton().removeState("invalid");
            }
        }
    },

    destruct : function() {
        this.__bf = null;
        this.__ps = null;
        //this._disposeObjects("__field_name");                                
    }
});