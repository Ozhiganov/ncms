/**
 * Actions repository
 */
qx.Class.define("ncms.Actions", {
    extend : sm.conn.Actions,

    construct : function() {
        this.base(arguments);
        this._testPrefix = "http://localhost:8080";
        this._resourceManager = qx.util.ResourceManager.getInstance();

        //Starting GUI state
        this._action("app.state", "/ncms/rs/adm/ws/state");

        //Logout
        this._action("app.logout", "/ncms/rs/adm/ws/logout");

        //Workspace components
        this._action("nav.selectors", "/ncms/rs/adm/ui/widgets/navigation-selectors");

        //Asm selector
        this._action("asms.select", "/ncms/rs/adm/asms/select");
        this._action("asms.select.count", "/ncms/rs/adm/asms/select/count");

        //Asm editor

        //GET/DELETE assembly
        this._action("asms", "/ncms/rs/adm/asms/{id}");

        //GET Get basic assembly info
        // name: assembly name
        this._action("asms.basic", "/ncms/rs/adm/asms/basic/{name}");

        //PUT create new assembly with specified name
        this._action("asms.new", "/ncms/rs/adm/asms/new/{name}");

        //PUT rename existing assembly
        this._action("asms.rename", "/ncms/rs/adm/asms/rename/{id}/{name}");

        //PUT Assembly parents
        this._action("asms.parents", "/ncms/rs/adm/asms/{id}/parents");

        //PUT Assembly core
        //Payload: JSON core spec
        this._action("asms.core", "/ncms/rs/adm/asms/{id}/core");

        //GET|DELETE Assembly attribute
        // id: assembly id
        // name: attribute name
        this._action("asms.attribute", "/ncms/rs/adm/asms/{id}/attribute/{name}");

        //PUT Assembly attribute values/options
        // id: assembly id
        this._action("asms.attributes", "/ncms/rs/adm/asms/{id}/attributes");

        //PUT Save assembly
        // id: assembly id
        this._action("asms.props", "/ncms/rs/adm/asms/{id}/props");

        //PUT Exchange attributes ordinals
        this._action("asms.attributes.exchange", "/ncms/rs/adm/asms/attributes/reorder/{ordinal1}/{ordinal2}");


        //==================== MediaRS

        //GET files only /ncms/rs/media/files/<path to folder>
        this._action("media.files", "/ncms/rs/media/files");

        //GET sub-folders only
        this._action("media.folders", "/ncms/rs/media/folders");

        //GET files + sub-folders
        this._action("media.all", "/ncms/rs/media/all");

        //GET select
        this._action("media.select", "/ncms/rs/media/select");

        //GET select count
        this._action("media.select.count", "/ncms/rs/media/select/count");

        //PUT create new folder with specifed name
        this._action("media.folder.put", "/ncms/rs/media/folder");

        //PUT /ncms/rs/media/rename/<path to folder>
        this._action("media.move", "/ncms/rs/media/move");

        //PUT  /ncms/rs/media/copy-batch/<path to folde>
        // Payload: JSON array of file paths to copy into {target} dir
        this._action("media.copy-batch", "/ncms/rs/media/copy-batch");

        //DELETE media file/folder
        this._action("media.delete", "/ncms/rs/media/delete");

        //DELETE media file/folder batch mode
        this._action("media.delete-batch", "/ncms/rs/media/delete-batch");

        //PUT Upload media file /ncms/rs/media/file/<path to file>
        this._action("media.upload", "/ncms/rs/media/file");

        //GET Download file /ncms/rs/media/file/<path to file>
        this._action("media.file", "/ncms/rs/media/file");

        //GET Thumbnail of image media
        this._action("media.thumbnail2", "/ncms/rs/media/thumbnail2/{id}");

        //GET Thumbnail of image media
        ///ncms/rs/media/thumbnail/<path to file>
        this._action("media.thumbnail", "/ncms/rs/media/thumbnail");

        //POST Save extra file meta fields
        this._action("media.meta", "/ncms/rs/media/meta/{id}");

        //POST Save extra file meta fields (by path)
        this._action("media.path.meta", "/ncms/rs/media/meta/path");

        //GET media-entity path by specified ID
        this._action("media.path", "/ncms/rs/media/path/{id}");

        //================== NcmsSecurityRS

        // GET/PUT/DELETE
        this._action("security.user", "/ncms/rs/adm/security/user/{name}");

        //GET selects users count
        this._action("security.users.count", "/ncms/rs/adm/security/users/count");

        //GET selects users
        this._action("security.users", "/ncms/rs/adm/security/users");

        // PUT/DELETE role/group for user
        this._action("security.user.role", "/ncms/rs/adm/security/user/{name}/role/{role}");
        this._action("security.user.group", "/ncms/rs/adm/security/user/{name}/group/{group}");


        //GET selects roles/groups
        this._action("security.roles", "/ncms/rs/adm/security/roles");
        this._action("security.groups", "/ncms/rs/adm/security/groups");

        //================== PagesRS

        //GET Pages tree layer
        ///ncms/rs/adm/pages/layer/{path:.*}
        this._action("pages.layer", "/ncms/rs/adm/pages/layer");

        //PUT Create new page
        // {
        //  name : {String} Page name
        //  parent : {Number?null} Parent page ID
        //  type: {String} Page type
        // }
        this._action("pages.new", "/ncms/rs/adm/pages/new");

        //DELETE The specified page
        this._action("pages.delete", "/ncms/rs/adm/pages/{id}");

        //GET Get page data for page-info pane.
        this._action("pages.info", "/ncms/rs/adm/pages/info/{id}");

        //GET|PUT page data for page-edit pane.
        this._action("pages.edit", "/ncms/rs/adm/pages/edit/{id}");

        //PUT set page owner
        // id: Page ID
        // owner: Owner user ID
        this._action("pages.owner", "/ncms/rs/adm/pages/owner/{id}/{owner}");

        //PUT page set template
        // {id}: Page ID
        // {templateId} Template ID
        this._action("pages.set.template", "/ncms/rs/adm/pages/template/{id}/{templateId}");

        // GET page acl
        // pid: page id
        this._action("pages.acl", "/ncms/rs/adm/pages/acl/{pid}");

        // PUT add new user to acl
        // DELETE delete user from acls for this page
        // POST update user rights
        // pid: page id
        // user: user login
        this._action("pages.acl.user", "/ncms/rs/adm/pages/acl/{pid}/{user}");

        //GET JSON Object contains full label-path and id-path to the specified page
        // {id}: Page ID
        this._action("pages.path", "/ncms/rs/adm/pages/path/{id}");

        // GET search page
        this._action("pages.search", "/ncms/rs/adm/pages/search");
        this._action("pages.search.count", "/ncms/rs/adm/pages/search/count");

        // GET get user access
        this._action("pages.get.rights", "/ncms/rs/adm/pages/rights/{pid}");
        // GET check user access
        this._action("pages.check.rights", "/ncms/rs/adm/pages/check/{pid}/{rights}");
    },

    members : {

        _testPrefix : null,

        _resourceManager : null,

        _action : function(id, path) {
            if (qx.core.Environment.get("ncms.testing.urls")) {
                this._addAction(id, this._resourceManager.toUri(this._testPrefix + path));
            } else {
                this._addAction(id, this._resourceManager.toUri(path));
            }
        }
    }
});
