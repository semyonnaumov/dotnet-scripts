FROM mcr.microsoft.com/dotnet/sdk:7.0.102-alpine3.17-amd64

WORKDIR /app

# Install dotnet-script runner
RUN dotnet tool install dotnet-script --tool-path /usr/bin && \
    dotnet-script --version

# Run dotnet-script with the specified file
ENTRYPOINT ["dotnet-script", "/tmp/script/main.csx", "-c", "release"]