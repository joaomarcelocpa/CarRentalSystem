"use client"

import type React from "react"
import LoginForm from "../components/LoginForm"

interface LoginPageProps {
    onClose: () => void
    onSwitchToRegister: () => void
}

const LoginPage: React.FC<LoginPageProps> = ({ onClose, onSwitchToRegister }) => {
    return <LoginForm onClose={onClose} onSwitchToRegister={onSwitchToRegister} />
}

export default LoginPage
