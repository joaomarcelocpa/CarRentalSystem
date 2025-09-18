export interface Agent {
    id?: string;
    username: string;
    email: string;
    corporateReason: string;
    cnpj: string;
}

export interface AgentCreateDTO {
    username: string;
    email: string;
    password: string;
    corporateReason: string;
    cnpj: string;
}

export interface AgentResponseDTO extends Agent {
    id: string;
}