import { BaseApiService } from './base-api.service';
import { API_CONFIG } from '../config/api';
import type { Income } from '../types/income';

export class IncomeService extends BaseApiService {
    async createIncome(income: Income): Promise<Income> {
        return this.post<Income>(API_CONFIG.ENDPOINTS.INCOMES, income);
    }
}