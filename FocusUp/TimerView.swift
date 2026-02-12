import SwiftUI

struct TimerView: View {
    let duration: TimerDuration
    @Binding var isPresented: Bool
    
    @State private var timeRemaining: Int
    @State private var timer: Timer? = nil
    @State private var showSticker = false
    @State private var earnedSticker = ""
    
    init(duration: TimerDuration, isPresented: Binding<Bool>) {
        self.duration = duration
        self._isPresented = isPresented
        self._timeRemaining = State(initialValue: duration.seconds)
    }
    
    var body: some View {
        ZStack {
            Color.black.edgesIgnoringSafeArea(.all)
            
            if showSticker {
                VStack(spacing: 30) {
                    Text("Focus Complete!")
                        .font(.largeTitle)
                        .fontWeight(.bold)
                        .foregroundColor(.white)
                    
                    Text(earnedSticker)
                        .font(.system(size: 100))
                    
                    Text("You earned a sticker!")
                        .font(.title2)
                        .foregroundColor(.white)
                    
                    Button(action: {
                        isPresented = false
                    }) {
                        Text("Done")
                            .font(.headline)
                            .foregroundColor(.white)
                            .frame(width: 200)
                            .padding()
                            .background(Color.blue)
                            .cornerRadius(10)
                    }
                    .padding(.top, 20)
                }
            } else {
                VStack(spacing: 40) {
                    Text("Stay Focused")
                        .font(.title)
                        .foregroundColor(.white)
                    
                    Text(timeString(from: timeRemaining))
                        .font(.system(size: 80, weight: .bold, design: .rounded))
                        .foregroundColor(.white)
                    
                    Button(action: {
                        stopTimer()
                        isPresented = false
                    }) {
                        Text("Cancel")
                            .font(.headline)
                            .foregroundColor(.white)
                            .frame(width: 200)
                            .padding()
                            .background(Color.red)
                            .cornerRadius(10)
                    }
                }
            }
        }
        .onAppear {
            startTimer()
        }
        .onDisappear {
            stopTimer()
        }
    }
    
    private func startTimer() {
        timer = Timer.scheduledTimer(withTimeInterval: 1.0, repeats: true) { _ in
            if timeRemaining > 0 {
                timeRemaining -= 1
            } else {
                stopTimer()
                completeTimer()
            }
        }
    }
    
    private func stopTimer() {
        timer?.invalidate()
        timer = nil
    }
    
    private func completeTimer() {
        earnedSticker = StickerBook.randomSticker()
        withAnimation {
            showSticker = true
        }
    }
    
    private func timeString(from seconds: Int) -> String {
        let hours = seconds / 3600
        let minutes = (seconds % 3600) / 60
        let secs = seconds % 60
        
        if hours > 0 {
            return String(format: "%02d:%02d:%02d", hours, minutes, secs)
        } else {
            return String(format: "%02d:%02d", minutes, secs)
        }
    }
}

struct TimerView_Previews: PreviewProvider {
    static var previews: some View {
        TimerView(duration: .fiveSeconds, isPresented: .constant(true))
    }
}
