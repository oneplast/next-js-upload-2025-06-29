import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  images: {
    dangerouslyAllowSVG: true,
    remotePatterns: [
      {
        protocol: "http",
        hostname: "k.kakaocdn.net",
      },
      {
        protocol: "https",
        hostname: "k.kakaocdn.net",
      },
      {
        protocol: "https",
        hostname: "placehold.co",
      },
    ],
    contentSecurityPolicy: "default-src 'self'; img-src 'self' data: https:;",
  },
  /* config options here */
};

export default nextConfig;
