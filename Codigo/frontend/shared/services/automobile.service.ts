import { BaseApiService } from './base-api.service';
import { API_CONFIG } from '@/shared/config/api';
import type { Automobile, AutomobileCreate, AutomobileResponse } from '@/shared/types/automobile';

export class AutomobileService extends BaseApiService {
    async getAllAutomobiles(): Promise<AutomobileResponse[]> {
        return this.get<AutomobileResponse[]>(API_CONFIG.ENDPOINTS.AUTOMOBILES);
    }

    async getAutomobileById(id: string): Promise<AutomobileResponse | null> {
        try {
            return await this.get<AutomobileResponse>(`${API_CONFIG.ENDPOINTS.AUTOMOBILES}/${id}`);
        } catch (error) {
            if (error instanceof Error && error.message.includes('404')) {
                return null;
            }
            throw error;
        }
    }

    async createAutomobile(automobile: AutomobileCreate): Promise<AutomobileResponse> {
        return this.post<AutomobileResponse>(API_CONFIG.ENDPOINTS.AUTOMOBILES, automobile);
    }

    async updateAutomobile(id: string, automobile: AutomobileCreate): Promise<AutomobileResponse | null> {
        try {
            return await this.put<AutomobileResponse>(`${API_CONFIG.ENDPOINTS.AUTOMOBILES}/${id}`, automobile);
        } catch (error) {
            if (error instanceof Error && error.message.includes('404')) {
                return null;
            }
            throw error;
        }
    }

    async deleteAutomobile(id: string): Promise<void> {
        await this.delete(`${API_CONFIG.ENDPOINTS.AUTOMOBILES}/${id}`);
    }

    async getAvailableAutomobiles(): Promise<AutomobileResponse[]> {
        const allAutomobiles = await this.getAllAutomobiles();
        return allAutomobiles.filter(auto => auto.available);
    }

    async checkAgentPermissions(): Promise<{canManageAutomobiles: boolean, agentUsername: string, authorities: string[]}> {
        return this.get<{canManageAutomobiles: boolean, agentUsername: string, authorities: string[]}>(`${API_CONFIG.ENDPOINTS.AUTOMOBILES}/agent/permissions`);
    }
}