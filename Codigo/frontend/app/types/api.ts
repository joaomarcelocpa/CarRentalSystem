export interface ApiResponse<T> {
    data: T;
    message?: string;
    success: boolean;
}

export const API_CONFIG = {
    BASE_URL: process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080/api',
    ENDPOINTS: {
        CUSTOMERS: '/customers',
        AUTOMOBILES: '/automobiles',
        RENTAL_REQUESTS: '/requests',
        RENTAL_CONTRACTS: '/contracts',
        CREDIT_CONTRACTS: '/credit-contracts',
        BANKS: '/banks',
        AGENTS: '/agents',
        INCOMES: '/incomes'
    }
};