module.exports = {
  devServer: {
    port: 8081,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },
  assetsDir: 'static',
  publicPath: './',
  //关闭eslint
  lintOnSave: false
}