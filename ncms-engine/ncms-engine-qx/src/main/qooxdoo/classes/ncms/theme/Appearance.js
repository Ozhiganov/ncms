/**
 * @asset(ncms/icon/16/actions/search.png)
 * @asset(ncms/icon/16/misc/cross-script.png)
 * @asset(qx/icon/${qx.icontheme}/16/actions/edit-clear.png)
 */

qx.Theme.define("ncms.theme.Appearance", {
    extend : qx.theme.simple.Appearance,
    include : [ sm.Appearance ],

    appearances : {

        "ncms-main-toolbar" : {
            alias : "toolbar",
            include : "toolbar"
        },

        "ncms-main-toolbar/part" : {
            include : "toolbar/part"
        },

        "ncms-tree-am" : {
        },

        "ncms-tree-am/tree" : "virtual-tree",


        /* Assemblies selector */
        "ncms-asm-selector" : {
            style : function(states) {
                return {
                }
            }
        },

        /* Media files selector */
        "ncms-mf-selector" : {
            style : function(states) {
                return {
                }
            }
        },

        //empty placeholder on workspace pane
        "ncms-wsa-placeholder" : {
            style : function(states) {
                return {
                    backgroundColor : "#dcdcdc"
                }
            }
        },

        //form on workspace pane
        "ncms-wsa-form" : {
            style : function(states) {
                return {
                    padding : [10, 50, 10, 10],
                    maxWidth : 600
                }
            }
        },

        "wiki-editor-toolbar-button" : "toolbar-table-button",

        "wiki-editor-toolbar-menubutton" : "toolbar-table-menubutton",

        // message info popup (on top of page, auto hide)
        "ncms-info-popup" : {
            style : function(states) {
                return {
                    padding : [5, 10, 5, 10],
                    maxWidth : 350,
                    backgroundColor : "background"
                }
            }
        }
    }
});