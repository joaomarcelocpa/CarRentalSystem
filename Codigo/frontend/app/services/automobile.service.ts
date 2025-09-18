import { BaseApiService } from './base-api.service';
import { API_CONFIG } from '../config/api';
import type { Automobile } from '../types/automobile';

export class AutomobileService extends BaseApiService {
    async getAllAutomobiles(): Promise<Automobile[]> {
        return this.get<Automobile[]>(API_CONFIG.ENDPOINTS.AUTOMOBILES);
    }

    async getAutomobileById(id: string): Promise<Automobile | null> {
        try {
            return await this.get<Automobile>(`${API_CONFIG.ENDPOINTS.AUTOMOBILES}/${id}`);
        } catch (error) {
            if (error instanceof Error && error.message.includes('404')) {
                return null;
            }
            throw error;
        }
    }

    async createAutomobile(automobile: Automobile): Promise<Automobile> {
        return this.post<Automobile>(API_CONFIG.ENDPOINTS.AUTOMOBILES, automobile);
    }

    async updateAutomobile(id: string, automobile: Automobile): Promise<Automobile | null> {
        try {
            return await this.put<Automobile>(`${API_CONFIG.ENDPOINTS.AUTOMOBILES}/${id}`, automobile);
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

    async getAvailableAutomobiles(): Promise<Automobile[]> {
        const allAutomobiles = await this.getAllAutomobiles();
        return allAutomobiles.filter(auto => auto.available);
    }
}