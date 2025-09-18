"use client"

import type React from "react"
import RegisterForm from "../../components/RegisterForm"

interface RegisterPageProps {
    onClose: () => void
    onSwitchToLogin: () => void
}

const RegisterPage: React.FC<RegisterPageProps> = ({ onClose, onSwitchToLogin }) => {
    return <RegisterForm onClose={onClose} onSwitchToLogin={onSwitchToLogin} />
}

export default RegisterPage
