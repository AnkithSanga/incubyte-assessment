import {
  Component,
  OnInit,
  AfterViewInit,
  ElementRef,
  ViewChild,
  signal,
  inject
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SalaryApiService, CreateEmployeePayload, UpdateEmployeePayload } from './core/services/salary-api.service';
import {
  OverallKpis,
  DepartmentAnalytics,
  CountryAnalytics,
  SalaryBandDistribution,
  Employee,
  AssistantQueryResponse
} from './core/models/salary.model';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit, AfterViewInit {
  private readonly apiService = inject(SalaryApiService);

  @ViewChild('deptChartCanvas') deptCanvas!: ElementRef<HTMLCanvasElement>;
  @ViewChild('countryChartCanvas') countryCanvas!: ElementRef<HTMLCanvasElement>;
  @ViewChild('bandChartCanvas') bandCanvas!: ElementRef<HTMLCanvasElement>;

  private deptChart: Chart | null = null;
  private countryChart: Chart | null = null;
  private bandChart: Chart | null = null;

  // Analytics & Filter Signals
  readonly kpis = signal<OverallKpis | null>(null);
  readonly departments = signal<DepartmentAnalytics[]>([]);
  readonly countries = signal<CountryAnalytics[]>([]);
  readonly salaryBands = signal<SalaryBandDistribution[]>([]);

  readonly departmentsList = signal<string[]>([]);
  readonly countriesList = signal<string[]>([]);

  // Directory Signals
  readonly employees = signal<Employee[]>([]);
  readonly totalElements = signal<number>(0);
  readonly totalPages = signal<number>(0);
  readonly currentPage = signal<number>(0);
  readonly pageSize = signal<number>(20);

  readonly selectedDepartment = signal<string>('');
  readonly selectedCountry = signal<string>('');
  readonly searchQuery = signal<string>('');
  readonly loading = signal<boolean>(true);

  // Modals & Assistant
  readonly isAssistantOpen = signal<boolean>(false);
  readonly isAddModalOpen = signal<boolean>(false);
  readonly isEditModalOpen = signal<boolean>(false);
  readonly isPrdModalOpen = signal<boolean>(false);

  readonly toastMessage = signal<string>('');

  // Add Employee Form State
  newFirstName = '';
  newLastName = '';
  newEmail = '';
  newDepartment = 'Engineering';
  newJobTitle = 'Software Engineer';
  newCountry = 'United States';
  newCity = 'San Francisco';
  newCurrency = 'USD';
  newSalary: number | null = 120000;

  // Edit Employee Form State
  editId = 0;
  editFullName = '';
  editDepartment = '';
  editJobTitle = '';
  editSalary: number | null = 0;
  editCurrency = 'USD';

  // Assistant State
  readonly assistantInput = signal<string>('');
  readonly assistantLoading = signal<boolean>(false);
  readonly assistantResponses = signal<AssistantQueryResponse[]>([]);

  readonly samplePrompts = [
    'What is the median salary in Engineering?',
    'How does ACME pay people in Germany?',
    'Which department has the highest payroll?',
    'What is our total global headcount and payroll?',
    'Compare pay in India vs United States'
  ];

  ngOnInit(): void {
    this.loadAnalytics();
    this.loadFilterMetadata();
    this.loadEmployees();
  }

  ngAfterViewInit(): void {
    // Charts will render as data loads
  }

  loadAnalytics(): void {
    this.apiService.getKpis().subscribe({
      next: (data) => this.kpis.set(data),
      error: (err) => console.error('Failed to load KPIs:', err)
    });

    this.apiService.getDepartmentAnalytics().subscribe({
      next: (data) => {
        this.departments.set(data);
        this.renderDeptChart(data);
      },
      error: (err) => console.error('Failed to load department analytics:', err)
    });

    this.apiService.getCountryAnalytics().subscribe({
      next: (data) => {
        this.countries.set(data);
        this.renderCountryChart(data);
      },
      error: (err) => console.error('Failed to load country analytics:', err)
    });

    this.apiService.getSalaryDistribution().subscribe({
      next: (data) => {
        this.salaryBands.set(data);
        this.renderBandChart(data);
      },
      error: (err) => console.error('Failed to load salary distribution:', err)
    });
  }

  loadFilterMetadata(): void {
    this.apiService.getDepartments().subscribe({
      next: (list) => this.departmentsList.set(list),
      error: (err) => console.error('Failed to load departments list:', err)
    });

    this.apiService.getCountries().subscribe({
      next: (list) => this.countriesList.set(list),
      error: (err) => console.error('Failed to load countries list:', err)
    });
  }

  loadEmployees(): void {
    this.loading.set(true);
    this.apiService.getEmployees({
      page: this.currentPage(),
      size: this.pageSize(),
      department: this.selectedDepartment() || undefined,
      country: this.selectedCountry() || undefined,
      search: this.searchQuery() || undefined
    }).subscribe({
      next: (res) => {
        this.employees.set(res.content || []);
        if (res.page) {
          this.totalElements.set(res.page.totalElements);
          this.totalPages.set(res.page.totalPages);
        } else if (res.totalElements !== undefined) {
          this.totalElements.set(res.totalElements);
          this.totalPages.set(res.totalPages ?? 0);
        }
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Failed to load employees:', err);
        this.loading.set(false);
      }
    });
  }

  onSearchChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.searchQuery.set(input.value);
    this.currentPage.set(0);
    this.loadEmployees();
  }

  onDepartmentChange(event: Event): void {
    const select = event.target as HTMLSelectElement;
    this.selectedDepartment.set(select.value);
    this.currentPage.set(0);
    this.loadEmployees();
  }

  onCountryChange(event: Event): void {
    const select = event.target as HTMLSelectElement;
    this.selectedCountry.set(select.value);
    this.currentPage.set(0);
    this.loadEmployees();
  }

  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages()) {
      this.currentPage.set(page);
      this.loadEmployees();
    }
  }

  // Add Employee Modal
  openAddModal(): void {
    this.newFirstName = '';
    this.newLastName = '';
    this.newEmail = '';
    this.newDepartment = 'Engineering';
    this.newJobTitle = 'Software Engineer';
    this.newCountry = 'United States';
    this.newCity = 'San Francisco';
    this.newCurrency = 'USD';
    this.newSalary = 120000;
    this.isAddModalOpen.set(true);
  }

  closeAddModal(): void {
    this.isAddModalOpen.set(false);
  }

  onCountrySelectChange(country: string): void {
    this.newCountry = country;
    const mapping: Record<string, { currency: string; city: string; defaultSalary: number }> = {
      'United States': { currency: 'USD', city: 'San Francisco', defaultSalary: 125000 },
      'Germany': { currency: 'EUR', city: 'Berlin', defaultSalary: 85000 },
      'United Kingdom': { currency: 'GBP', city: 'London', defaultSalary: 75000 },
      'India': { currency: 'INR', city: 'Bengaluru', defaultSalary: 2400000 },
      'Japan': { currency: 'JPY', city: 'Tokyo', defaultSalary: 9500000 },
      'Canada': { currency: 'CAD', city: 'Toronto', defaultSalary: 95000 }
    };
    if (mapping[country]) {
      this.newCurrency = mapping[country].currency;
      this.newCity = mapping[country].city;
      this.newSalary = mapping[country].defaultSalary;
    }
  }

  saveNewEmployee(): void {
    if (!this.newFirstName.trim() || !this.newLastName.trim() || !this.newEmail.trim() || !this.newSalary) {
      alert('Please fill in all required fields.');
      return;
    }

    const payload: CreateEmployeePayload = {
      firstName: this.newFirstName.trim(),
      lastName: this.newLastName.trim(),
      email: this.newEmail.trim(),
      department: this.newDepartment,
      jobTitle: this.newJobTitle,
      country: this.newCountry,
      city: this.newCity,
      currencyCode: this.newCurrency,
      annualBaseSalaryLocal: this.newSalary
    };

    this.apiService.createEmployee(payload).subscribe({
      next: (created) => {
        this.closeAddModal();
        this.showToast(`Successfully added ${created.fullName} (${created.employeeId})!`);
        this.loadEmployees();
        this.loadAnalytics();
      },
      error: (err) => {
        console.error('Failed to create employee:', err);
        alert('Failed to save employee. Please check values.');
      }
    });
  }

  // Edit Employee Modal
  openEditModal(emp: Employee): void {
    this.editId = emp.id;
    this.editFullName = emp.fullName;
    this.editDepartment = emp.department;
    this.editJobTitle = emp.jobTitle;
    this.editSalary = emp.annualBaseSalaryLocal;
    this.editCurrency = emp.currencyCode;
    this.isEditModalOpen.set(true);
  }

  closeEditModal(): void {
    this.isEditModalOpen.set(false);
  }

  saveEditEmployee(): void {
    if (!this.editSalary || this.editSalary <= 0) {
      alert('Please enter a valid salary amount.');
      return;
    }

    const payload: UpdateEmployeePayload = {
      department: this.editDepartment,
      jobTitle: this.editJobTitle,
      annualBaseSalaryLocal: this.editSalary
    };

    this.apiService.updateEmployee(this.editId, payload).subscribe({
      next: (updated) => {
        this.closeEditModal();
        this.showToast(`Updated compensation for ${updated.fullName}!`);
        this.loadEmployees();
        this.loadAnalytics();
      },
      error: (err) => {
        console.error('Failed to update employee:', err);
        alert('Failed to update employee.');
      }
    });
  }

  // Delete Employee
  deleteEmployee(emp: Employee): void {
    const confirmDelete = confirm(`Are you sure you want to delete ${emp.fullName} (${emp.employeeId})?`);
    if (!confirmDelete) return;

    this.apiService.deleteEmployee(emp.id).subscribe({
      next: () => {
        this.showToast(`Employee ${emp.fullName} removed.`);
        this.loadEmployees();
        this.loadAnalytics();
      },
      error: (err) => {
        console.error('Failed to delete employee:', err);
        alert('Failed to delete employee.');
      }
    });
  }

  // Export Filtered CSV
  exportToCsv(): void {
    this.apiService.getEmployees({
      page: 0,
      size: 500,
      department: this.selectedDepartment() || undefined,
      country: this.selectedCountry() || undefined,
      search: this.searchQuery() || undefined
    }).subscribe({
      next: (res) => {
        const rows = res.content || [];
        if (rows.length === 0) {
          alert('No employee records to export.');
          return;
        }

        const headers = ['Employee ID', 'Full Name', 'Email', 'Department', 'Job Title', 'Country', 'City', 'Currency', 'Annual Base Salary (Local)', 'Annual Base Salary (USD)'];
        const csvContent = [
          headers.join(','),
          ...rows.map(e => [
            `"${e.employeeId}"`,
            `"${e.fullName}"`,
            `"${e.email}"`,
            `"${e.department}"`,
            `"${e.jobTitle}"`,
            `"${e.country}"`,
            `"${e.city}"`,
            `"${e.currencyCode}"`,
            e.annualBaseSalaryLocal,
            e.annualBaseSalaryUsd
          ].join(','))
        ].join('\n');

        const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.setAttribute('href', url);
        link.setAttribute('download', `acme-salary-records-${new Date().toISOString().slice(0, 10)}.csv`);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        this.showToast(`Exported ${rows.length} records to CSV!`);
      }
    });
  }

  showToast(msg: string): void {
    this.toastMessage.set(msg);
    setTimeout(() => this.toastMessage.set(''), 3500);
  }

  // PRD Modal
  openPrdModal(): void {
    this.isPrdModalOpen.set(true);
  }

  closePrdModal(): void {
    this.isPrdModalOpen.set(false);
  }

  // Assistant Logic
  toggleAssistant(): void {
    this.isAssistantOpen.update((v) => !v);
  }

  askAssistant(question?: string): void {
    const q = question ?? this.assistantInput();
    if (!q || !q.trim()) return;

    this.assistantLoading.set(true);
    this.apiService.askAssistant(q).subscribe({
      next: (res) => {
        this.assistantResponses.update((history) => [res, ...history]);
        this.assistantInput.set('');
        this.assistantLoading.set(false);
      },
      error: (err) => {
        console.error('Assistant query failed:', err);
        this.assistantLoading.set(false);
      }
    });
  }

  // Chart Renderers
  private renderDeptChart(data: DepartmentAnalytics[]): void {
    if (!this.deptCanvas?.nativeElement) return;
    if (this.deptChart) this.deptChart.destroy();

    const labels = data.map((d) => d.department);
    const avgSalaries = data.map((d) => d.averageSalaryUsd);

    this.deptChart = new Chart(this.deptCanvas.nativeElement, {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [{
          label: 'Average Annual Base Salary (USD)',
          data: avgSalaries,
          backgroundColor: 'rgba(59, 130, 246, 0.85)',
          hoverBackgroundColor: '#3b82f6',
          borderRadius: 8,
          maxBarThickness: 42
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: {
            callbacks: {
              label: (ctx) => `Avg Salary: $${Math.round(ctx.raw as number).toLocaleString()}`
            }
          }
        },
        scales: {
          y: {
            ticks: {
              color: '#9ca3af',
              callback: (val) => `$${Number(val) / 1000}k`
            },
            grid: { color: 'rgba(255, 255, 255, 0.05)' }
          },
          x: {
            ticks: { color: '#9ca3af', font: { size: 12, weight: 'bold' } },
            grid: { display: false }
          }
        }
      }
    });
  }

  private renderCountryChart(data: CountryAnalytics[]): void {
    if (!this.countryCanvas?.nativeElement) return;
    if (this.countryChart) this.countryChart.destroy();

    const labels = data.map((c) => c.country);
    const shares = data.map((c) => c.payrollPercentage);

    this.countryChart = new Chart(this.countryCanvas.nativeElement, {
      type: 'doughnut',
      data: {
        labels: labels,
        datasets: [{
          data: shares,
          backgroundColor: [
            '#3b82f6',
            '#8b5cf6',
            '#10b981',
            '#f59e0b',
            '#06b6d4',
            '#f43f5e'
          ],
          borderWidth: 3,
          borderColor: '#111827'
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        cutout: '68%',
        plugins: {
          legend: {
            position: 'right',
            labels: { color: '#e5e7eb', font: { size: 12 }, boxWidth: 14, padding: 12 }
          },
          tooltip: {
            callbacks: {
              label: (ctx) => `${ctx.label}: ${ctx.raw}% of payroll`
            }
          }
        }
      }
    });
  }

  private renderBandChart(data: SalaryBandDistribution[]): void {
    if (!this.bandCanvas?.nativeElement) return;
    if (this.bandChart) this.bandChart.destroy();

    const labels = data.map((b) => b.label);
    const counts = data.map((b) => b.count);

    this.bandChart = new Chart(this.bandCanvas.nativeElement, {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [{
          label: 'Employees in Band',
          data: counts,
          backgroundColor: 'rgba(139, 92, 246, 0.85)',
          hoverBackgroundColor: '#8b5cf6',
          borderRadius: 8,
          maxBarThickness: 56
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: {
            callbacks: {
              label: (ctx) => `${(ctx.raw as number).toLocaleString()} Employees (${((ctx.raw as number) / 100).toFixed(1)}%)`
            }
          }
        },
        scales: {
          y: {
            ticks: { color: '#9ca3af' },
            grid: { color: 'rgba(255, 255, 255, 0.05)' }
          },
          x: {
            ticks: { color: '#9ca3af', font: { size: 12, weight: 'bold' } },
            grid: { display: false }
          }
        }
      }
    });
  }

  // Formatting Helpers
  formatUsd(amount: number | null | undefined): string {
    if (amount == null) return '$0';
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      maximumFractionDigits: 0
    }).format(amount);
  }

  formatLocal(amount: number, currency: string): string {
    return `${amount.toLocaleString()} ${currency}`;
  }
}
