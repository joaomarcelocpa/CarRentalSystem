export type UserType = "cliente" | "agente-empresa" | "agente-banco"

export interface User {
    id: string
    name: string
    email: string
    userType: UserType
    isLoggedIn: boolean
}

export interface AuthContextType {
    user: User | null
    login: (email: string, password: string) => Promise<boolean>
    logout: () => void
    register: (name: string, email: string, password: string, userType: UserType) => Promise<boolean>
    isLoading?: boolean
    clearAllData?: () => void
    getRegisteredUsers?: () => Array<{
        id: string
        name: string
        email: string
        userType: UserType
    }>
}