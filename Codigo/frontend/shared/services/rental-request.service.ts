import { BaseApiService } from './base-api.service';
import { API_CONFIG } from '@/shared/config/api';
import type {
    RentalRequestCreateDTO,
    RentalRequestResponseDTO,
    RentalRequestUpdateDTO,
    RentalRequestStatusUpdateDTO,
    RequestStatus
} from '@/shared/types/rental-request';

export class RentalRequestService extends BaseApiService {
    // Endpoint para criar pedido (CLIENTE)
    async createRentalRequest(request: RentalRequestCreateDTO): Promise<RentalRequestResponseDTO> {
        return this.post<RentalRequestResponseDTO>('/rental-requests', request);
    }

    // Endpoint para listar pedidos do cliente
    async getMyRequests(): Promise<RentalRequestResponseDTO[]> {
        return this.get<RentalRequestResponseDTO[]>('/rental-requests/my-requests');
    }

    // Endpoint para buscar pedido específico
    async getRentalRequestById(id: string): Promise<RentalRequestResponseDTO> {
        return this.get<RentalRequestResponseDTO>(`/rental-requests/${id}`);
    }

    // Endpoint para atualizar pedido (CLIENTE)
    async updateRentalRequest(id: string, update: RentalRequestUpdateDTO): Promise<RentalRequestResponseDTO> {
        return this.put<RentalRequestResponseDTO>(`/rental-requests/${id}`, update);
    }

    // Endpoint para cancelar pedido (CLIENTE)
    async cancelRequest(id: string): Promise<RentalRequestResponseDTO> {
        return this.patch<RentalRequestResponseDTO>(`/rental-requests/${id}/cancel`, {});
    }

    // Endpoint para deletar pedido (CLIENTE)
    async deleteRequest(id: string): Promise<void> {
        return this.delete(`/rental-requests/${id}`);
    }

    // ========== ENDPOINTS PARA AGENTES ==========

    // Listar todos os pedidos (AGENTE)
    async getAllRentalRequests(): Promise<RentalRequestResponseDTO[]> {
        return this.get<RentalRequestResponseDTO[]>('/rental-requests/all');
    }

    // Listar pedidos pendentes (AGENTE)
    async getPendingRequests(): Promise<RentalRequestResponseDTO[]> {
        return this.get<RentalRequestResponseDTO[]>('/rental-requests/pending');
    }

    // Listar pedidos dos veículos do agente (AGENTE_EMPRESA)
    async getRequestsForMyAutomobiles(): Promise<RentalRequestResponseDTO[]> {
        return this.get<RentalRequestResponseDTO[]>('/rental-requests/agent/my-automobiles');
    }

    // Atualizar status do pedido (AGENTE)
    async updateRequestStatus(id: string, statusUpdate: RentalRequestStatusUpdateDTO): Promise<RentalRequestResponseDTO> {
        return this.put<RentalRequestResponseDTO>(`/rental-requests/${id}/status`, statusUpdate);
    }

    // Buscar estatísticas (AGENTE)
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
        // Usar o endpoint correto que filtra pelo cliente
        return this.getMyRequests();
    }
}