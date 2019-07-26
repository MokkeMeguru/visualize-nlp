const path = require('path');
module.exports = {
    entry:  "./src/app.ts",
    output: {
        path: `${__dirname}/dist`,
        filename: 'app.js'
    },
    module: {
        rules: [
            {
                test: /\.tsx?$/,
                use: "ts-loader"
            }
        ]
    },
    resolve: {
        extensions: [".ts", ".js", ".tsx", ".json"]
    },
    devServer : {
        contentBase: `${__dirname}/dist`,
        port: 3449
    },
};
