import { BaseApiService } from './base-api.service';
import { API_CONFIG } from '@/shared/config/api';
import type { RentalRequest, RequestStatus } from '@/shared/types/rental-request';
import type { RentalRequestCreate } from '@/shared/types/flexible-types';

export class RentalRequestService extends BaseApiService {
    async getAllRentalRequests(): Promise<RentalRequest[]> {
        return this.get<RentalRequest[]>(API_CONFIG.ENDPOINTS.RENTAL_REQUESTS);
    }

    async getRentalRequestById(id: string): Promise<RentalRequest | null> {
        try {
            return await this.get<RentalRequest>(`${API_CONFIG.ENDPOINTS.RENTAL_REQUESTS}/${id}`);
        } catch (error) {
            if (error instanceof Error && error.message.includes('404')) {
                return null;
            }
            throw error;
        }
    }

    async createRentalRequest(request: RentalRequest | RentalRequestCreate): Promise<RentalRequest> {
        // Transform the request to match backend expectations
        const requestData = {
            desiredStartDate: request.desiredStartDate,
            desiredEndDate: request.desiredEndDate,
            observations: request.observations,
            customer: request.customer,
            automobile: request.automobile
        };

        return this.post<RentalRequest>(API_CONFIG.ENDPOINTS.RENTAL_REQUESTS, requestData);
    }

    async updateRentalRequestStatus(id: string, status: RequestStatus): Promise<RentalRequest | null> {
        try {
            return await this.patch<RentalRequest>(`${API_CONFIG.ENDPOINTS.RENTAL_REQUESTS}/${id}/status?status=${status}`, {});
        } catch (error) {
            if (error instanceof Error && error.message.includes('404')) {
                return null;
            }
            throw error;
        }
    }

    async deleteRentalRequest(id: string): Promise<void> {
        await this.delete(`${API_CONFIG.ENDPOINTS.RENTAL_REQUESTS}/${id}`);
    }

    async getRentalRequestsByCustomer(customerId: string): Promise<RentalRequest[]> {
        const allRequests = await this.getAllRentalRequests();
        return allRequests.filter(request => request.customer?.id === customerId);
    }
}