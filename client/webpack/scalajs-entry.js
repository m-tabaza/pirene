if (process.env.NODE_ENV === "production") {
    const opt = require("./pirene-client-opt.js");
    opt.main();
    module.exports = opt;
} else {
    var exports = window;
    exports.require = require("./pirene-client-fastopt-entrypoint.js").require;
    window.global = window;

    const fastOpt = require("./pirene-client-fastopt.js");
    fastOpt.main()
    module.exports = fastOpt;

    if (module.hot) {
        module.hot.accept();
    }
}
