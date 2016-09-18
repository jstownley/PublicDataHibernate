function solution = getNthDegLeastSquares(degree, a, b)

    % Initialize the solution
    solution = [];
    
    % Check the degree
    if (1 > degree) 
        disp('The degree must be at least 1');
        return;
    end

    % Check the sizes of the arrays
    if (length(a) ~= length(b)) 
        disp('Cannot perform least squares with arrays of unequal length');
        return;
    end

    % Form the A matrix
    A = zeros(length(a),degree+1);
    for jj = 1:degree+1
        A(:,jj) = a.^(jj-1);
    end

    % Do the math: output = ((A'A)^-1)A'b where ' denotes the Transpose function
    At = A';
    AtA = matrixMultMxN(At,A);
    AtAinv = inverse(AtA);
    AtAinvAt = matrixMultMxN(AtAinv,At);
    solution = matrixMultMx1(AtAinvAt,b);


    return;
    
    
function outMatrix = matrixMultMxN(a, b)
    % Get the dimensions of the inputs
    dimsA = size(a);
    dimsB = size(b);

    % We can do MxN*NxL matrix multiplication by doing Mx1 matrix multiplication
    % over each column of b.  However, selecting b(1) will select the first row.
    % We can get around this by first taking the transpose of b and then
    % selecting the first row of the transposed bT matrix.
    bT = b';

    % Do the math
    outMatrix = zeros(dimsA(1),dimsB(2));
    for jj = 1:dimsB(2)
        thisCol = matrixMultMx1(a,bT(jj,:));
        for ii = 1:dimsA(1)
            outMatrix(ii,jj) = thisCol(ii);
        end
    end
    
    return;
    

function outArray = matrixMultMx1(a, b)
    % Get the dimensions of the inputs
    dimsA = size(a);
    dimB = length(b);

    % Check for matrix dimension agreement
    if (dimsA(2) ~= dimB)
        disp('Improper matrix dimension agreement.  Matrices should be of size MxN and Nx1');
        outArray = [];
        return;
    end

    % Do the math
    outArray = zeros(dimsA(1),1);
    for ii = 1:dimsA(1)
        % Initialize the output value for this element
%         outArray(ii) = 0.0;
        for jj = 1:dimB
            outArray(ii) = outArray(ii) + a(ii,jj) * b(jj);
        end

    end
    
    return;
    

function x = linearSolve(A, b)
    % Check the dimensions of the inputs
    dimsA = size(A);
    dimB = length(b);
    if (dimsA(1) ~= dimB)
        disp('Improper matrix sizes.  A must be MxN and b must be Mx1');
        x = [];
        return;
    end

    % Initialize the output array
    x = zeros(dimB,1);
    d = zeros(dimB,1);

    % Perform the LU Decomposition of A
    [L,U] = lucrout(A);

    % Calculate the solution to Ld=b
    d(1) = b(1)/L(1,1);

    for ii = 1:dimB
        sum = 0.0;
        for jj = 1:ii
            sum = sum + L(ii,jj) * d(jj);
        end
        d(ii) = (b(ii) - sum) / L(ii,ii);
    end

    % Calculate the solution to Ux=d
    x(dimB) = d(dimB);
    for ii = dimB-1:-1:1
        sum = 0.0;
        for jj = ii+1:dimB
            sum = sum + U(ii,jj) * x(jj);
        end
        x(ii) = d(ii) - sum;
    end

    return;
    

function nvrs = inverse(a)
    % Get the matrix dimensions for a
    dims = size(a);

    % Set up the Identy matrix
    I = eye(dims(1));

    % Solve for the inverse
    nvrs = zeros(dims(1));
    for jj = 1:dims(1)
        thisCol = linearSolve(a,I(:,jj));
        for ii = 1:dims(1)
            nvrs(ii,jj) = thisCol(ii);
        end
    end

    return;
    
    
function [L,U] = lucrout(A)
	[~,n] = size(A);
	L = zeros(n,n);
	U = eye(n,n);
	L(1,1) = A(1,1);
	for j=2:n
		L(j,1) = A(j,1);
		U(1,j) = A(1,j) / L(1,1);
	end
	for j=2:n-1
		for i=j:n
			L(i,j) = A(i,j);
			for k=1:j-1
				L(i,j) = L(i,j) - L(i,k)*U(k,j);
			end
		end
		for k=j+1:n
			U(j,k) = A(j,k);
			for i=1:j-1
				U(j,k) = U(j,k) - L(j,i)*U(i,k);
			end
			U(j,k) = U(j,k) / L(j,j);
		end
	end
	L(n,n) = A(n,n);
	for k=1:n-1
		L(n,n) = L(n,n) - L(n,k)*U(k,n);
    end
    
	return;