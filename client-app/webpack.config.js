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
                test: /\.ts$/,
                use: "ts-loader"
            }
        ]
    },
    resolve: {
        extensions: [".ts", "js"]
    },
    devServer : {
        contentBase: `${__dirname}/dist`
    },
};
