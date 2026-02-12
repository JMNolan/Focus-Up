import SwiftUI

enum TimerDuration: String, CaseIterable {
    case fiveSeconds = "5 Seconds (Testing)"
    case fifteenMinutes = "15 Minutes"
    case thirtyMinutes = "30 Minutes"
    case oneHour = "1 Hour"
    case twoHours = "2 Hours"
    
    var seconds: Int {
        switch self {
        case .fiveSeconds:
            return 5
        case .fifteenMinutes:
            return 15 * 60
        case .thirtyMinutes:
            return 30 * 60
        case .oneHour:
            return 60 * 60
        case .twoHours:
            return 2 * 60 * 60
        }
    }
}

struct ContentView: View {
    @State private var selectedDuration: TimerDuration? = nil
    @State private var showTimer = false
    
    var body: some View {
        NavigationView {
            VStack(spacing: 30) {
                Spacer()
                
                Text("Focus Up")
                    .font(.largeTitle)
                    .fontWeight(.bold)
                
                Text("Select your focus duration")
                    .font(.headline)
                    .foregroundColor(.secondary)
                
                Picker("Select Timer", selection: $selectedDuration) {
                    Text("Select a timer").tag(nil as TimerDuration?)
                    ForEach(TimerDuration.allCases, id: \.self) { duration in
                        Text(duration.rawValue).tag(duration as TimerDuration?)
                    }
                }
                .pickerStyle(MenuPickerStyle())
                .padding()
                .background(Color.gray.opacity(0.1))
                .cornerRadius(10)
                .padding(.horizontal)
                
                Spacer()
                
                Button(action: {
                    showTimer = true
                }) {
                    Text("Start Focus Session")
                        .font(.headline)
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(selectedDuration == nil ? Color.gray : Color.blue)
                        .cornerRadius(10)
                }
                .padding(.horizontal)
                .padding(.bottom, 40)
                .disabled(selectedDuration == nil)
                .fullScreenCover(isPresented: $showTimer) {
                    if let duration = selectedDuration {
                        TimerView(duration: duration, isPresented: $showTimer)
                    }
                }
            }
            .navigationBarHidden(true)
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
