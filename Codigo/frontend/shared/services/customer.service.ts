import { BaseApiService } from './base-api.service';
import { API_CONFIG } from '@/shared/config/api';
import type { CustomerCreateDTO, CustomerResponseDTO } from '@/shared/types/customer';

export class CustomerService extends BaseApiService {
    async getAllCustomers(): Promise<CustomerResponseDTO[]> {
        return this.get<CustomerResponseDTO[]>(API_CONFIG.ENDPOINTS.CUSTOMERS);
    }

    async getCustomerById(id: string): Promise<CustomerResponseDTO | null> {
        try {
            return await this.get<CustomerResponseDTO>(`${API_CONFIG.ENDPOINTS.CUSTOMERS}/${id}`);
        } catch (error) {
            if (error instanceof Error && error.message.includes('404')) {
                return null;
            }
            throw error;
        }
    }

    async updateCreditLimit(id: string, creditLimit: number): Promise<CustomerResponseDTO> {
    return this.patch<CustomerResponseDTO>(`${API_CONFIG.ENDPOINTS.CUSTOMERS}/${id}/credit-limit`, { creditLimit });
}

    async createCustomer(customer: CustomerCreateDTO): Promise<CustomerResponseDTO> {
        return this.post<CustomerResponseDTO>(API_CONFIG.ENDPOINTS.CUSTOMERS, customer);
    }

    async updateCustomer(id: string, customer: CustomerCreateDTO): Promise<CustomerResponseDTO | null> {
        try {
            return await this.put<CustomerResponseDTO>(`${API_CONFIG.ENDPOINTS.CUSTOMERS}/${id}`, customer);
        } catch (error) {
            if (error instanceof Error && error.message.includes('404')) {
                return null;
            }
            throw error;
        }
    }

    async deleteCustomer(id: string): Promise<void> {
        await this.delete(`${API_CONFIG.ENDPOINTS.CUSTOMERS}/${id}`);
    }
}