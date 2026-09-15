import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { App } from './app';
import { SalaryApiService } from './core/services/salary-api.service';

describe('App Component', () => {
  let mockApiService: any;

  beforeEach(async () => {
    mockApiService = {
      getKpis: () => of({
        totalEmployees: 10000,
        totalPayrollUsd: 921943250,
        averageSalaryUsd: 92194,
        medianSalaryUsd: 88665,
        minSalaryUsd: 9720,
        maxSalaryUsd: 266000,
        highestPayingDepartment: 'Engineering',
        highestPayingCountry: 'United States'
      }),
      getDepartmentAnalytics: () => of([
        { department: 'Engineering', headcount: 1460, totalPayrollUsd: 159599435, averageSalaryUsd: 109314, minSalaryUsd: 13560, maxSalaryUsd: 266000, payrollPercentage: 17.3 }
      ]),
      getCountryAnalytics: () => of([
        { country: 'United States', currencyCode: 'USD', headcount: 1686, totalPayrollUsd: 247105500, averageSalaryUsd: 146563, minSalaryUsd: 75000, maxSalaryUsd: 266000, payrollPercentage: 26.8 }
      ]),
      getSalaryDistribution: () => of([
        { label: '< $50K', minUsd: 0, maxUsd: 50000, count: 1690, percentage: 16.9 }
      ]),
      getDepartments: () => of(['Engineering', 'Product', 'Sales', 'Finance']),
      getCountries: () => of(['United States', 'Germany', 'United Kingdom', 'India']),
      getEmployees: () => of({
        content: [
          {
            id: 1,
            employeeId: 'EMP-00001',
            firstName: 'Priya',
            lastName: 'Johnson',
            fullName: 'Priya Johnson',
            email: 'priya.johnson1@acme.corp',
            department: 'Finance',
            jobTitle: 'Payroll Specialist',
            country: 'United States',
            city: 'San Francisco',
            currencyCode: 'USD',
            annualBaseSalaryLocal: 112500,
            annualBaseSalaryUsd: 112500
          }
        ],
        page: { size: 20, number: 0, totalElements: 1, totalPages: 1 }
      })
    };

    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        { provide: SalaryApiService, useValue: mockApiService }
      ]
    }).compileComponents();
  });

  it('should create the App component', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should render the application title', async () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
    await fixture.whenStable();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.brand-title')?.textContent).toContain('ACME Compensation Intelligence');
  });

  it('should format USD currency correctly', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    expect(app.formatUsd(100000)).toBe('$100,000');
    expect(app.formatUsd(null)).toBe('$0');
  });

  it('should format local currency with symbol and value', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    const formattedEur = app.formatLocal(85000, 'EUR');
    expect(formattedEur).toContain('EUR');
    expect(formattedEur).toContain('85,000');
  });

  it('should auto-map country selection to native currency and default city', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;

    app.onCountrySelectChange('Germany');
    expect(app.newCurrency).toBe('EUR');
    expect(app.newCity).toBe('Berlin');

    app.onCountrySelectChange('India');
    expect(app.newCurrency).toBe('INR');
    expect(app.newCity).toBe('Bengaluru');
  });
});
