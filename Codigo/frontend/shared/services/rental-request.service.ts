import { BaseApiService } from './base-api.service';
import type {
    RentalRequestCreateDTO,
    RentalRequestResponseDTO,
    RentalRequestUpdateDTO,
    RentalRequestStatusUpdateDTO
} from '@/shared/types/rental-request';

export class RentalRequestService extends BaseApiService {
    async createRentalRequest(request: RentalRequestCreateDTO): Promise<RentalRequestResponseDTO> {
        return this.post<RentalRequestResponseDTO>('/rental-requests', request);
    }

    async getMyRequests(): Promise<RentalRequestResponseDTO[]> {
        return this.get<RentalRequestResponseDTO[]>('/rental-requests/my-requests');
    }

    async getRentalRequestById(id: string): Promise<RentalRequestResponseDTO> {
        return this.get<RentalRequestResponseDTO>(`/rental-requests/${id}`);
    }

    async updateRentalRequest(id: string, update: RentalRequestUpdateDTO): Promise<RentalRequestResponseDTO> {
        return this.put<RentalRequestResponseDTO>(`/rental-requests/${id}`, update);
    }

    async cancelRequest(id: string): Promise<RentalRequestResponseDTO> {
        return this.patch<RentalRequestResponseDTO>(`/rental-requests/${id}/cancel`, {});
    }

    async deleteRequest(id: string): Promise<void> {
        return this.delete(`/rental-requests/${id}`);
    }

    // ========== ENDPOINTS PARA AGENTES ==========

    async getAllRentalRequests(): Promise<RentalRequestResponseDTO[]> {
        return this.get<RentalRequestResponseDTO[]>('/rental-requests/all');
    }

    async getPendingRequests(): Promise<RentalRequestResponseDTO[]> {
        return this.get<RentalRequestResponseDTO[]>('/rental-requests/pending');
    }

    async getRequestsForMyAutomobiles(): Promise<RentalRequestResponseDTO[]> {
        return this.get<RentalRequestResponseDTO[]>('/rental-requests/agent/my-automobiles');
    }

    async updateRequestStatus(id: string, statusUpdate: RentalRequestStatusUpdateDTO): Promise<RentalRequestResponseDTO> {
        return this.put<RentalRequestResponseDTO>(`/rental-requests/${id}/status`, statusUpdate);
    }

    async getStatistics(): Promise<{
        total: number;
        pending: number;
        approved: number;
        rejected: number;
        active: number;
        completed: number;
        cancelled: number;
    }> {
        return this.get('/rental-requests/statistics');
    }

    // ========== MÉTODOS AUXILIARES ==========

    async getRentalRequestsByCustomer(customerId: string): Promise<RentalRequestResponseDTO[]> {
        return this.getMyRequests();
    }
}