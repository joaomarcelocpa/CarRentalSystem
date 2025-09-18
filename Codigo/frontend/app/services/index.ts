import {CustomerService} from "@/app/services/customer.service";
import {AutomobileService} from "@/app/services/automobile.service";
import {RentalRequestService} from "@/app/services/rental-request.service";
import {RentalContractService} from "@/app/services/rental-contract.service";
import {CreditContractService} from "@/app/services/credit-contract.service";
import {BankService} from "@/app/services/bank.service";
import {AgentService} from "@/app/services/agent.service";
import {IncomeService} from "@/app/services/income.service";

export class ApiService {
    public static customer = new CustomerService();
    public static automobile = new AutomobileService();
    public static rentalRequest = new RentalRequestService();
    public static rentalContract = new RentalContractService();
    public static creditContract = new CreditContractService();
    public static bank = new BankService();
    public static agent = new AgentService();
    public static income = new IncomeService();
}