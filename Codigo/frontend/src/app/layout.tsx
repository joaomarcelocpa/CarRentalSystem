import type React from "react"
import type { Metadata } from "next"
import { Analytics } from "@vercel/analytics/next"
import { AuthProvider } from "@/contexts/AuthContext"
import { Suspense } from "react"
import { Inter } from "next/font/google"
import "./globals.css"

export const metadata: Metadata = {
    title: "RentalCarSystem - Aluguel de Carros",
    description: "Sistema completo de aluguel de carros com gestão de clientes e agentes",
    generator: "v0.app",
}

const inter = Inter({subsets: ["latin"]})

export default function RootLayout({children}: Readonly<{children: React.ReactNode}>) {
    return (
        <html lang="pt-BR">
        <body className={inter.className}>
        <AuthProvider>
            <Suspense fallback={<div>Loading...</div>}>
                {children}
            </Suspense>
            <Analytics />
        </AuthProvider>
        </body>
        </html>
    )
}

