import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";
import { LibraryProvider } from "@/contexts/LibraryContext";

const inter = Inter({ subsets: ["latin"] });

export const metadata: Metadata = {
  title: "Davon Library System",
  description: "A modern library management system",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en">
      <body className={inter.className}>
        <LibraryProvider>
          {children}
        </LibraryProvider>
      </body>
    </html>
  );
}
