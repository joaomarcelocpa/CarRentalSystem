export type UserRole = "CUSTOMER" | "AGENT_COMPANY" | "AGENT_BANK";

export interface UserCreateDTO {
    username: string;
    email: string;
    password: string;
    role: UserRole;
}

export interface UserResponseDTO {
    id: string;
    username: string;
    email: string;
    role: UserRole;
    createdAt: string;
}

export interface LoginRequestDTO {
    username: string;
    password: string;
}

export interface LoginResponseDTO {
    token: string;
    type: string;
    username: string;
    email: string;
    role: UserRole;
    expiresIn: number;
}
