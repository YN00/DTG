import React from 'react';

import { RecoilRoot } from 'recoil';
import { QueryClient, QueryClientProvider } from 'react-query';
import AppInner from './AppInner';

const queryClient = new QueryClient();

const App = () => {
  return (
    <RecoilRoot>
      <QueryClientProvider client={queryClient}>
        <AppInner />
      </QueryClientProvider>
    </RecoilRoot>
  );
};

export default App;
