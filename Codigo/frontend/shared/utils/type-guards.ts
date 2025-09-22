import type { CustomerSummary, AutomobileSummary } from '@/shared/types/summary';
import type { CustomerComplete, AutomobileComplete } from '@/shared/types/flexible-types';

// Type guards to check if objects have required properties
export function isCustomerComplete(customer: any): customer is CustomerComplete {
    return customer &&
        typeof customer.id === 'string' &&
        typeof customer.name === 'string' &&
        typeof customer.emailContact === 'string';
}

export function isAutomobileComplete(automobile: any): automobile is AutomobileComplete {
    return automobile &&
        typeof automobile.id === 'string' &&
        typeof automobile.brand === 'string' &&
        typeof automobile.model === 'string' &&
        typeof automobile.year === 'number' &&
        typeof automobile.dailyRate === 'number';
}

export function isCustomerSummary(customer: any): customer is CustomerSummary {
    return customer && typeof customer.id === 'string';
}

export function isAutomobileSummary(automobile: any): automobile is AutomobileSummary {
    return automobile && typeof automobile.id === 'string';
}

// Utility functions to safely get display values
export function getCustomerDisplayName(customer: CustomerSummary | undefined): string {
    if (!customer) return 'Cliente não especificado';
    return customer.name || customer.emailContact || `Cliente ${customer.id}`;
}

export function getAutomobileDisplayName(automobile: AutomobileSummary | undefined): string {
    if (!automobile) return 'Veículo não especificado';
    if (automobile.brand && automobile.model) {
        return `${automobile.brand} ${automobile.model}`;
    }
    return `Veículo ${automobile.id}`;
}

export function getAutomobileFullInfo(automobile: AutomobileSummary | undefined): string {
    if (!automobile) return 'Veículo não especificado';

    const name = getAutomobileDisplayName(automobile);
    const year = automobile.year ? ` (${automobile.year})` : '';
    const price = automobile.dailyRate ? ` - R$ ${automobile.dailyRate}/dia` : '';

    return `${name}${year}${price}`;
}

// Validation utilities
export function validateRentalRequestData(data: any): string[] {
    const errors: string[] = [];

    if (!data.desiredStartDate) {
        errors.push('Data de início é obrigatória');
    }

    if (!data.desiredEndDate) {
        errors.push('Data de término é obrigatória');
    }

    if (data.desiredStartDate && data.desiredEndDate) {
        const startDate = new Date(data.desiredStartDate);
        const endDate = new Date(data.desiredEndDate);

        if (endDate <= startDate) {
            errors.push('Data de término deve ser posterior à data de início');
        }

        if (startDate < new Date()) {
            errors.push('Data de início não pode ser anterior a hoje');
        }
    }

    if (!data.customer?.id) {
        errors.push('Cliente é obrigatório');
    }

    if (!data.automobile?.id) {
        errors.push('Veículo é obrigatório');
    }

    return errors;
}

// Format utilities
export function formatCurrency(value: number | undefined): string {
    if (value === undefined || value === null) {
        return 'Valor não definido';
    }
    return new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL'
    }).format(value);
}

export function formatDate(dateString: string | undefined): string {
    if (!dateString) {
        return 'Data não definida';
    }
    return new Date(dateString).toLocaleDateString('pt-BR');
}

export function calculateDaysBetween(startDate: string | undefined, endDate: string | undefined): number {
    if (!startDate || !endDate) {
        return 0;
    }
    const start = new Date(startDate);
    const end = new Date(endDate);
    const diffTime = Math.abs(end.getTime() - start.getTime());
    return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
}

export function getStatusLabel(status: string | undefined): string {
    if (!status) {
        return 'Status não definido';
    }
    const labels: { [key: string]: string } = {
        'CREATED': 'Criada',
        'UNDER_ANALYSIS': 'Em Análise',
        'APPROVED': 'Aprovada',
        'REJECTED': 'Rejeitada',
        'CANCELLED': 'Cancelada',
        'EXECUTED': 'Executada'
    };
    return labels[status] || status;
}

// Safe calculation utilities
export function safeCalculateEstimatedValue(
    dailyRate: number | undefined,
    startDate: string | undefined,
    endDate: string | undefined
): number {
    if (!dailyRate || !startDate || !endDate) {
        return 0;
    }
    const days = calculateDaysBetween(startDate, endDate);
    return days * dailyRate;
}

// Safe number operations
export function safeNumber(value: number | undefined, defaultValue: number = 0): number {
    return value !== undefined && value !== null && !isNaN(value) ? value : defaultValue;
}

export function safeString(value: string | undefined, defaultValue: string = ''): string {
    return value !== undefined && value !== null ? value : defaultValue;
}