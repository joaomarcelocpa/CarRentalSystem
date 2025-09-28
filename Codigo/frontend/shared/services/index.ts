import {CustomerService} from "@/shared/services/customer.service";
import {AutomobileService} from "@/shared/services/automobile.service";
import {RentalRequestService} from "@/shared/services/rental-request.service";
import {RentalContractService} from "@/shared/services/rental-contract.service";
import {CreditContractService} from "@/shared/services/credit-contract.service";
import {BankService} from "@/shared/services/bank.service";
import {AgentService} from "@/shared/services/agent.service";
import {IncomeService} from "@/shared/services/income.service";
import {AuthService} from "@/shared/services/auth.service";

export class ApiService {
    public static customer = new CustomerService();
    public static automobile = new AutomobileService();
    public static rentalRequest = new RentalRequestService();
    public static rentalContract = new RentalContractService();
    public static creditContract = new CreditContractService();
    public static bank = new BankService();
    public static agent = new AgentService();
    public static income = new IncomeService();
    public static auth = new AuthService();
}