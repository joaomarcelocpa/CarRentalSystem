import { BaseApiService } from './base-api.service';
import { API_CONFIG } from '@/shared/config/api';
import type { AgentCreateDTO, AgentResponseDTO } from '@/shared/types/agent';

export class AgentService extends BaseApiService {
    async getAllAgents(): Promise<AgentResponseDTO[]> {
        return this.get<AgentResponseDTO[]>(API_CONFIG.ENDPOINTS.AGENTS);
    }

    async getAgentById(id: string): Promise<AgentResponseDTO | null> {
        try {
            return await this.get<AgentResponseDTO>(`${API_CONFIG.ENDPOINTS.AGENTS}/${id}`);
        } catch (error) {
            if (error instanceof Error && error.message.includes('404')) {
                return null;
            }
            throw error;
        }
    }

    async createAgent(agent: AgentCreateDTO): Promise<AgentResponseDTO> {
        return this.post<AgentResponseDTO>(API_CONFIG.ENDPOINTS.AGENTS, agent);
    }

    async getAgentByEmail(email: string): Promise<AgentResponseDTO | null> {
        try {
            const agents = await this.getAllAgents();
            return agents.find(agent => agent.email === email) || null;
        } catch (error) {
            return null;
        }
    }
}