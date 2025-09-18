import { BaseApiService } from './base-api.service';
import { API_CONFIG } from '../config/api';
import type { RentalContract } from '../types/rental-contract';

export class RentalContractService extends BaseApiService {
    async createRentalContract(contract: RentalContract): Promise<RentalContract> {
        return this.post<RentalContract>(API_CONFIG.ENDPOINTS.RENTAL_CONTRACTS, contract);
    }
}