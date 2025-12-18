declare module "dotenv" {
  interface DotenvConfigOptions {
    path?: string;
    encoding?: string;
    debug?: boolean;
  }

  interface DotenvConfigOutput {
    error?: Error;
    parsed?: Record<string, string>;
  }

  export function config(options?: DotenvConfigOptions): DotenvConfigOutput;
}

