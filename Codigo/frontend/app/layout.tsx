import type React from "react"
import type { Metadata } from "next"
import { Analytics } from "@vercel/analytics/next"
import { AuthProvider } from "@/shared/contexts/AuthContext"
import { Suspense } from "react"
import "./globals.css"

export const metadata: Metadata = {
    title: "RentalCarSystem - Aluguel de Carros",
    description: "Sistema completo de aluguel de carros com gestão de clientes e agentes",
    generator: "v0.app",
}

export default function RootLayout({
                                       children,
                                   }: Readonly<{
    children: React.ReactNode
}>) {
    return (
        <html lang="pt-BR">
        <body className={`font-sans`}>
        <Suspense fallback={<div>Loading...</div>}>
            <AuthProvider>{children}</AuthProvider>
        </Suspense>
        <Analytics />
        </body>
        </html>
    )
}
