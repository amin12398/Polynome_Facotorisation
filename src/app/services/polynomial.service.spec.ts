import { TestBed } from '@angular/core/testing';

import { PolynomialService } from './polynomial.service';

describe('PolynomialService', () => {
  let service: PolynomialService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PolynomialService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
