/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  swcMinify: true,
  output: 'standalone',
  // This ensures assets are properly served when accessing via IP address
  assetPrefix: process.env.NODE_ENV === 'production' ? undefined : '',
}

module.exports = nextConfig 