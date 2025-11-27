import { defineConfig } from 'vite'

export default defineConfig({
  // Server configuration for the Vite development server
  server: {
    // Proxy configuration - redirects API requests during development
    // This solves CORS issues by making the frontend and backend appear to be on the same origin
    proxy: {
      // Any request starting with '/api' will be proxied
      '/api': {
        // Target server URL - where the requests should be forwarded to
        // In this case, the Spring Boot backend running on port 8080
        target: 'http://localhost:8080',

        // Change the origin header to match the target URL
        // This is necessary because the backend might check the origin header
        // and reject requests that don't come from an expected origin
        changeOrigin: true,

        // Advanced proxy configuration to handle connection lifecycle
        // This ensures proper cleanup of connections and error handling
        configure: (proxy) => {
          proxy.on("error", (err, req, res) => {
            console.log("error connection upstream")
            res.writeHead(502)
            res.end()
          })
          proxy.on("proxyRes", (proxyRes, _, res) => {
            const upstreamSocket = proxyRes.socket
            console.log("upstream connected")
            if(upstreamSocket) {
              upstreamSocket.once('close', () => {
                console.log("upstream closed")
                if(!res.writableFinished) {
                  console.log("destroying downstream")
                  res.destroy()
                }
              })
            }
          })
        },
      },
    },
  },
})
